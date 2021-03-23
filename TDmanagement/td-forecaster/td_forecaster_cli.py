# -*- coding: utf-8 -*-
"""
@author: tsoukj
"""

import argparse
import sys
import os
import json
from model_training import build_and_train_td, build_and_train_td_file_level

#===============================================================================
# td_system_level_forecasting ()
#===============================================================================
def td_system_level_forecasting(horizon_param_td, project_param_td, regressor_param_td, ground_truth_param_td, write_file_param_td):
    """
    API Call to td_system_level_forecasting service
    Arguments:
        horizon_param_td: Required (sent as parameter from command line)
        project_param_td: Required (sent as parameter from command linel)
        regressor_param_td: Required (sent as parameter from command line)
        ground_truth_param_td: Optional (sent as parameter from command line)
        write_file_param_td: Optional (sent as parameter from command line)
    """

    # Call build_and_train() function and retrieve forecasts
    results = build_and_train_td(horizon_param_td, project_param_td, regressor_param_td, ground_truth_param_td)
    if results is -1:
        print('%s steps-ahead forecasting cannot provide reliable results for this project. Please reduce forecasting horizon.' % horizon_param_td)
    elif results is -2:
        print('Could not find "%s.csv" in "/data" directory. System-level analysis is currently unavailable for this project.' % project_param_td)
    else:
        # Jsonify and print respond or write to file
        if write_file_param_td:
            with open('output/%s_forecasts.json' % project_param_td, 'w', encoding='utf-8') as f:
                json.dump(results, f, ensure_ascii=False, indent=4)
            print('Output saved to "output/%s_forecasts.json"' % project_param_td)
        else:
            print(json.dumps(results, indent=4))

#===============================================================================
# td_file_level_forecasting ()
#===============================================================================
def td_file_level_forecasting(horizon_param_td, project_param_td, project_files_param_td, regressor_param_td, ground_truth_param_td, write_file_param_td):
    """
    API Call to td_file_level_forecasting service
    Arguments:
        horizon_param_td: Required (sent as parameter from command line)
        project_param_td: Required (sent as parameter from command line)
        project_files_param_td: Required (sent as parameter from command line)
        regressor_param_td: Required (sent as parameter from command line)
        ground_truth_param_td: Optional (sent as parameter from command line)
        write_file_param_td: Optional (sent as parameter from command line)
    """

    # Call build_and_train_td_file_level() function and retrieve forecasts
    results = build_and_train_td_file_level(horizon_param_td, project_param_td, project_files_param_td, regressor_param_td, ground_truth_param_td)
    if results is -1:
        print('%s steps-ahead forecasting cannot provide reliable results for this project. Please reduce forecasting horizon.' % horizon_param_td)
    elif results is -2:
        print('Could not find "%s_class.csv" in "/data" directory. File-level analysis is currently unavailable for this project.' % project_param_td)
    else:
        # Jsonify and print respond or write to file
        if write_file_param_td:
            with open('output/%s_forecasts_class.json' % project_param_td, 'w', encoding='utf-8') as f:
                json.dump(results, f, ensure_ascii=False, indent=4)
            print('Output saved to "output/%s_forecasts_class.json"' % project_param_td)
        else:
            print(json.dumps(results, indent=4))

#===============================================================================
# run_server ()
#===============================================================================
def run_server(granularity, horizon, project, files, regressor, ground_truth, write_file, debug_mode):
    """
    Executes the command to start the server
    Arguments:
        granularity: retrieved from create_arg_parser() as a string
        horizon: retrieved from create_arg_parser() as a int
        project: retrieved from create_arg_parser() as a string
        files: retrieved from create_arg_parser() as a int
        regressor: retrieved from create_arg_parser() as a string
        ground_truth: retrieved from create_arg_parser() as a bool
        debug_mode: retrieved from create_arg_parser() as a bool
    """

    print('Granularity:  %s' % (granularity))
    print('Project:      %s' % (project))
    print('Horizon:      %s' % (horizon))
    print('Regressor:    %s' % (regressor))
    print('Files:        %s' % (files))
    print('Ground truth: %s' % (ground_truth))
    print('Write file:   %s' % (write_file))
    print('Debug mode:   %s' % (debug_mode))

    # Store settings in environment variables
    if debug_mode:
        print(" *** Debug enabled! ***")
    os.environ['DEBUG'] = str(debug_mode)
    
    if ground_truth:
        ground_truth = 'yes'
    else:
        ground_truth = 'no'

    if granularity == 'system':
        # Run td_system_level_forecasting
        td_system_level_forecasting(horizon, project, regressor, ground_truth, write_file)
    elif granularity == 'file':
        # Run td_file_level_forecasting
        td_file_level_forecasting(horizon, project, files, regressor, ground_truth, write_file)

#===============================================================================
# create_arg_parser ()
#===============================================================================
def create_arg_parser():
    """
    Creates the parser to retrieve arguments from the command line
    Returns:
        A Parser object
    """
    regressors = ['auto', 'mlr', 'lasso', 'ridge', 'svr_linear', 'svr_rbf', 'random_forest']
    granularity = ['system', 'file']

    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument('g', metavar='GRANULARITY', help='The level of granularity for which the forecasts will be produced. Valid inputs: '+', '.join(granularity), choices=granularity, type=str)
    parser.add_argument('h', metavar='HORIZON', help='The forecasting horizon up to which forecasts will be produced (e.g. "10").', type=int)
    parser.add_argument('p', metavar='PROJECT', help='The name of the project for which the forecasts will be produced (e.g. "metalwalls_measures"). A csv file with the same name (containing comma separated values of TD metrics to be given as input to the model) should exist within the "/data" folder.', type=str)
    parser.add_argument('f', metavar='FILE_NUM', help='The number of files for which the forecasts will be produced during file-level forecasting (e.g. "10").', type=int)
    parser.add_argument('r', metavar='REGRESSOR', help='The regressor models that will be used to produce forecasts. Valid inputs: '+', '.join(regressors), choices=regressors, type=str)
    parser.add_argument('--ground_truth', help='Return ground truth values in addition to forecasts', action='store_true', default=False)
    parser.add_argument('--write_file', help='Write the output to a json file stored in "/output" folder instead of printing to the console.', action='store_true', default=False)
    parser.add_argument('--debug', help='Run builtin server in debug mode', action='store_true', default=False)

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
    granularity = args.g
    horizon = args.h
    project = args.p
    files = args.f
    regressor = args.r
    ground_truth = args.ground_truth
    write_file = args.write_file
    debug_mode = args.debug

    # Run server with user-given arguments
    run_server(granularity, horizon, project, files, regressor, ground_truth, write_file, debug_mode)

if __name__ == '__main__':
    main()
