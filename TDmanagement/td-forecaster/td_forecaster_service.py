# -*- coding: utf-8 -*-
"""
@author: tsoukj
"""

import argparse
import sys
import os
from flask import Flask, jsonify, request
from flask_cors import CORS
from waitress import serve
from model_training import build_and_train_td, build_and_train_td_file_level

# Create the Flask app
app = Flask(__name__)
# Enable CORS
CORS(app)

#===============================================================================
# td_system_level_forecasting ()
#===============================================================================
@app.route('/TDForecaster/SystemForecasting', methods=['GET'])
def td_system_level_forecasting(horizon_param_td=None, project_param_td=None, regressor_param_td=None, ground_truth_param_td=None):
    """
    API Call to td_system_level_forecasting service
    Arguments:
        horizon_param_td: Required (sent as URL query parameter from API Call)
        project_param_td: Required (sent as URL query parameter from API Call)
        regressor_param_td: Optional (sent as URL query parameter from API Call)
        ground_truth_param_td: Optional (sent as URL query parameter from API Call)
    Returns:
        A JSON containing the forecasting results, status code and a message.
    """

    # Parse URL-encoded parameters
    horizon_param_td = request.args.get('horizon', type=int) # Required: if key doesn't exist, returns None
    project_param_td = request.args.get('project', type=str) # Required: if key doesn't exist, returns None
    regressor_param_td = request.args.get('regressor', default='auto', type=str) # Optional: if key doesn't exist, returns auto
    ground_truth_param_td = request.args.get('ground_truth', default='no', type=str) # Optional: if key doesn't exist, returns no

    # If required parameters are missing from URL
    if horizon_param_td is None or project_param_td is None or regressor_param_td is None or ground_truth_param_td is None:
        return unprocessable_entity()
    else:
        # Call build_and_train() function and retrieve forecasts
        results = build_and_train_td(horizon_param_td, project_param_td, regressor_param_td, ground_truth_param_td)
        if results is -1:
            return internal_server_error('%s steps-ahead forecasting cannot provide reliable results for this project. Please reduce forecasting horizon.' % horizon_param_td)

        # Compose and jsonify respond
        message = {
            'status': 200,
            'message': 'The request was fulfilled.',
            'results': results,
    	}
        resp = jsonify(message)
        resp.status_code = 200

        return resp

#===============================================================================
# td_file_level_forecasting ()
#===============================================================================
@app.route('/TDForecaster/FileForecasting', methods=['GET'])
def td_file_level_forecasting(horizon_param_td=None, project_param_td=None, project_files_param_td=None, regressor_param_td=None, ground_truth_param_td=None):
    """
    API Call to td_file_level_forecasting service
    Arguments:
        horizon_param_td: Required (sent as URL query parameter from API Call)
        project_param_td: Required (sent as URL query parameter from API Call)
        project_files_param_td: Required (sent as URL query parameter from API Call)
        regressor_param_td: Optional (sent as URL query parameter from API Call)
        ground_truth_param_td: Optional (sent as URL query parameter from API Call)
    Returns:
        A JSON containing the forecasting results, status code and a message.
    """

    # Parse URL-encoded parameters
    horizon_param_td = request.args.get('horizon', type=int) # Required: if key doesn't exist, returns None
    project_param_td = request.args.get('project', type=str) # Required: if key doesn't exist, returns None
    project_files_param_td = request.args.get('project_files', type=int) # Required: if key doesn't exist, returns None
    regressor_param_td = request.args.get('regressor', default='auto', type=str) # Optional: if key doesn't exist, returns auto
    ground_truth_param_td = request.args.get('ground_truth', default='no', type=str) # Optional: if key doesn't exist, returns no

    # If required parameters are missing from URL
    if horizon_param_td is None or project_param_td is None or project_files_param_td is None or regressor_param_td is None or ground_truth_param_td is None:
        return unprocessable_entity()
    else:
        # Call build_and_train_td_file_level() function and retrieve forecasts
        results = build_and_train_td_file_level(horizon_param_td, project_param_td, project_files_param_td, regressor_param_td, ground_truth_param_td)
        if results is -1:
            return internal_server_error('%s steps-ahead forecasting cannot provide reliable results for this project. Please reduce forecasting horizon.' % horizon_param_td)
        if results is -2:
            return internal_server_error('File-level analysis is currently unavailable for this project.')

        # Compose and jsonify respond
        message = {
            'status': 200,
            'message': 'The request was fulfilled.',
            'results': results,
    	}
        resp = jsonify(message)
        resp.status_code = 200

        return resp

#===============================================================================
# errorhandler ()
#===============================================================================
@app.errorhandler(400)
def bad_request(error=None):
    message = {
        'status': 400,
        'message': 'Bad Request: ' + request.url + ' --> Please check your data payload.',
    }
    resp = jsonify(message)
    resp.status_code = 400

    return resp
@app.errorhandler(422)
def unprocessable_entity(error=None):
    message = {
        'status': 400,
        'message': 'Unprocessable Entity: ' + request.url + ' --> Missing or invalid parameters.',
    }
    resp = jsonify(message)
    resp.status_code = 400

    return resp
@app.errorhandler(500)
def internal_server_error(error=None):
    message = {
        'status': 500,
        'message': 'The server encountered an internal error and was unable to complete your request. ' + error,
    }
    resp = jsonify(message)
    resp.status_code = 500

    return resp

#===============================================================================
# run_server ()
#===============================================================================
def run_server(host, port, mode, debug_mode):
    """
    Executes the command to start the server
    Arguments:
        host: retrieved from create_arg_parser() as a string
        port: retrieved from create_arg_parser() as a int
        mode: retrieved from create_arg_parser() as a string
        debug_mode: retrieved from create_arg_parser() as a bool
    """

    print('server:      %s:%s' % (host, port))
    print('mode:        %s' % (mode))
    print('debug_mode:  %s' % (debug_mode))

    # Store settings in environment variables
    if debug_mode:
        print(" *** Debug enabled! ***")

    os.environ['DEBUG'] = str(debug_mode)

    if mode == 'builtin':
        # Run app in debug mode using flask
        app.run(host, port, debug_mode)
    elif mode == 'waitress':
        # Run app in production mode using waitress
        serve(app, host=host, port=port)
    else:
        print('Server mode "%s" not yet implemented' % mode)
        sys.exit(1)

#===============================================================================
# create_arg_parser ()
#===============================================================================
def create_arg_parser():
    """
    Creates the parser to retrieve arguments from the command line
    Returns:
        A Parser object
    """
    server_modes = ['builtin', 'waitress']

    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument('h', metavar='HOST', help='Server HOST (e.g. "localhost")', type=str)
    parser.add_argument('p', metavar='PORT', help='Server PORT (e.g. "5001")', type=int)
    parser.add_argument('m', metavar='SERVER_MODE', help=", ".join(server_modes), choices=server_modes, type=str)
    parser.add_argument('--debug', help="Run builtin server in debug mode", action='store_true', default=False)

    return parser

#===============================================================================
# main ()
#===============================================================================
def main():
    """
    The main() function of the script acting as the entry point
    """
    parser = create_arg_parser()

    # If script run without arguments, print syntax
    if len(sys.argv) == 1:
        parser.print_help()
        sys.exit(1)

    # Parse arguments
    args = parser.parse_args()
    host = args.h
    mode = args.m
    port = args.p
    debug_mode = args.debug

    # Run server with user-given arguments
    run_server(host, port, mode, debug_mode)

if __name__ == '__main__':
    main()
