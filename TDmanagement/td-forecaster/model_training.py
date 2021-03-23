# -*- coding: utf-8 -*-
"""
@author: tsoukj
"""

import os
import pandas as pd
from sklearn.model_selection import GridSearchCV, TimeSeriesSplit, cross_validate
from sklearn.linear_model import LinearRegression, Lasso, Ridge
from sklearn.svm import SVR
from sklearn.ensemble import RandomForestRegressor
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import make_scorer
from sklearn.exceptions import ConvergenceWarning
from utils import mean_absolute_percentage_error, root_mean_squared_error, series_to_supervised

debug = bool(os.environ.get('DEBUG'))

#===============================================================================
# grid_search_best ()
#===============================================================================
def grid_search_best(reg_type, x_array, y_array):
    """
    Perform Grid Search on a model and return best hyper-parameters based on R2
    error minimization.
    Arguments:
        reg_type: Type of regressor as a string.
        x_array: Independent variable values of observations as a NumPy array.
        y_array: Dependent variable values of observations as a NumPy array.
    Returns:
        The best model hyper-parameters as a dict.
    """

    # Chosing hyperparameters based on best score during TimeSeriesSplit Validation
    splits = int(len(x_array) / 30) if len(x_array) >= 60 else 2
    tscv = TimeSeriesSplit(n_splits=splits)
    scaler = StandardScaler()

    # Create the regressor model and parameters range
    if reg_type == 'lasso':
        regressor = Lasso()
        pipeline = Pipeline([('regressor', regressor)])
        parameters = {'regressor__alpha': [0.00001, 0.0001, 0.001, 0.01, 0.1, 1, 10, 100, 1000, 100000, 1000000, 10000000]}
    elif reg_type == 'ridge':
        regressor = Ridge()
        pipeline = Pipeline([('regressor', regressor)])
        parameters = {'regressor__alpha': [0.00001, 0.0001, 0.001, 0.01, 0.1, 1, 10, 100, 1000, 100000, 1000000, 10000000]}
    elif reg_type == 'svr_linear':
        # Fitting linear SVR to the dataset
        regressor = SVR(kernel='linear')
        pipeline = Pipeline([('scaler', scaler), ('regressor', regressor)])
        parameters = {'regressor__C': [0.0001, 0.001, 0.01, 0.1, 1, 10, 100, 1000, 10000, 100000]}
    elif reg_type == 'svr_rbf':
        # Fitting SVR to the dataset
        regressor = SVR(kernel='rbf')
        pipeline = Pipeline([('scaler', scaler), ('regressor', regressor)])
        parameters = {'regressor__C': [0.0001, 0.001, 0.01, 0.1, 1, 10, 100, 1000, 10000, 100000], 'regressor__gamma' : [1, 0.1, 0.01, 0.001]}
    elif reg_type == 'random_forest':
        # Fitting Random Forest Regression to the dataset
        regressor = RandomForestRegressor()
        pipeline = Pipeline([('regressor', regressor)])
        parameters = {'regressor__n_estimators' : [5, 10, 100, 500], 'regressor__max_depth': [5, 10]}

    # Perform Grid Search
    grid_search = GridSearchCV(pipeline, parameters, cv=tscv)
    grid_search = grid_search.fit(x_array, y_array.ravel())

    # best_accuracy = grid_search.best_score_
    best_parameters = grid_search.best_params_

    if debug:
        print('=========================== Grid Search ===========================')
        print(' - Regressor: ', reg_type)
        print(' - Best Parameters: ', best_parameters)

    return best_parameters

#===============================================================================
# cross_validation_best ()
#===============================================================================
def cross_validation_best(pipes, x_array, y_array):
    """
    Perform TimeSeriesSplit Validation to a list of models and return best based
    on R2 error minimization.
    Arguments:
        pipelines: A list of models integrated into a Pipeline.
        x_array: Independent variable values of observations as a NumPy array.
        y_array: Dependent variable values of observations as a NumPy array.
    Returns:
        The best model integrated into a Pipeline.
    """

    # Chosing regressor based on best score during TimeSeriesSplit Validation
    splits = int(len(x_array) / 30) if len(x_array) >= 60 else 2
    tscv = TimeSeriesSplit(n_splits=splits)

    # Scores that will be computed during TimeSeriesSplit Validation
    scorer = {'neg_mean_absolute_error': 'neg_mean_absolute_error', 'neg_mean_squared_error': 'neg_mean_squared_error', 'r2': 'r2', 'mean_absolute_percentage_error': make_scorer(mean_absolute_percentage_error, greater_is_better=False), 'root_mean_squared_error': make_scorer(root_mean_squared_error, greater_is_better=False)}

    # Perform TimeSeriesSplit Validation and compute metrics
    best_score = float('-inf')
    best_regressor = None
    for pipe in pipes:
        scores = cross_validate(estimator=pipe, X=x_array, y=y_array.ravel(), scoring=scorer, cv=tscv, return_train_score=False)
        if scores['test_r2'].mean() > best_score:
            best_score = scores['test_r2'].mean()
            best_regressor = pipe

    if debug:
        print('=================== TimeSeriesSplit Validation ====================')
        print(' - Best Regressor: ', best_regressor['regressor'])
        print(' - Best R2 Score: ', best_score)

    return best_regressor

#===============================================================================
# create_regressor ()
#===============================================================================
def create_regressor(reg_type, x_array, y_array):
    """
    Create and train a regressor based on given X and Y values. Regressor type
    can be provided manually by the user or selected automatically based on R2
    error minimization.
    Arguments:
        reg_type: Type of regressor as a string.
        x_array: Independent variable values of observations as a NumPy array.
        y_array: Dependent variable values of observations as a NumPy array.
    Returns:
        A fitted sklearn model integrated into a Pipeline.
    """

    scaler = StandardScaler()

    # Create the regressor model
    try:
        if reg_type == 'mlr':
            # Fitting Multiple Linear Regression to the Training set
            regressor = LinearRegression()
            pipeline = Pipeline([('regressor', regressor)])
            pipeline.fit(x_array, y_array.ravel())
        elif reg_type == 'lasso':
            # Fitting Lasso Regression to the Training set
            best_parameters = grid_search_best('lasso', x_array, y_array)
            regressor = Lasso(alpha=best_parameters['regressor__alpha'])
            pipeline = Pipeline([('regressor', regressor)])
            pipeline.fit(x_array, y_array.ravel())
        elif reg_type == 'ridge':
            # Fitting Ridge Regression to the Training set
            best_parameters = grid_search_best('ridge', x_array, y_array)
            regressor = Ridge(alpha=best_parameters['regressor__alpha'])
            pipeline = Pipeline([('regressor', regressor)])
            pipeline.fit(x_array, y_array.ravel())
        elif reg_type == 'svr_linear':
            # Fitting linear SVR to the dataset
            best_parameters = grid_search_best('svr_linear', x_array, y_array)
            regressor = SVR(kernel='linear', C=best_parameters['regressor__C'])
            pipeline = Pipeline([('scaler', scaler), ('regressor', regressor)])
            pipeline.fit(x_array, y_array.ravel())
        elif reg_type == 'svr_rbf':
            # Fitting SVR to the dataset
            best_parameters = grid_search_best('svr_rbf', x_array, y_array)
            regressor = SVR(kernel='rbf', gamma=best_parameters['regressor__gamma'], C=best_parameters['regressor__C'])
            pipeline = Pipeline([('scaler', scaler), ('regressor', regressor)])
            pipeline.fit(x_array, y_array.ravel())
        elif reg_type == 'random_forest':
            # Fitting Random Forest Regression to the dataset
            best_parameters = grid_search_best('random_forest', x_array, y_array)
            regressor = RandomForestRegressor(n_estimators=best_parameters['regressor__n_estimators'], max_depth=best_parameters['regressor__max_depth'], random_state=0)
            pipeline = Pipeline([('regressor', regressor)])
            pipeline.fit(x_array, y_array.ravel())
        elif reg_type == 'auto':
            # Fitting Multiple Linear Regression to the Training set
            regressor_linear = LinearRegression()
            # Fitting Lasso Regression to the Training set
            best_parameters = grid_search_best('lasso', x_array, y_array)
            regressor_lasso = Lasso(alpha=best_parameters['regressor__alpha'])
            # Fitting Ridge Regression to the Training set
            best_parameters = grid_search_best('ridge', x_array, y_array)
            regressor_ridge = Ridge(alpha=best_parameters['regressor__alpha'])
            # Fitting linear SVR to the dataset
            best_parameters = grid_search_best('svr_linear', x_array, y_array)
            regressor_svr_linear = SVR(kernel='linear', C=best_parameters['regressor__C'])
            # Fitting SVR to the dataset
            best_parameters = grid_search_best('svr_rbf', x_array, y_array)
            regressor_svr_rbf = SVR(kernel='rbf', gamma=best_parameters['regressor__gamma'], C=best_parameters['regressor__C'])
            # Fitting Random Forest Regression to the dataset
            best_parameters = grid_search_best('random_forest', x_array, y_array)
            regressor_random_forest = RandomForestRegressor(n_estimators=best_parameters['regressor__n_estimators'], max_depth=best_parameters['regressor__max_depth'], random_state=0)
            # Perform TimeSeriesSplit Validation and return best model
            pipes = [Pipeline([('regressor', regressor_linear)]), Pipeline([('regressor', regressor_lasso)]), Pipeline([('regressor', regressor_ridge)]), Pipeline([('scaler', scaler), ('regressor', regressor_svr_linear)]), Pipeline([('scaler', scaler), ('regressor', regressor_svr_rbf)]), Pipeline([('regressor', regressor_random_forest)])]
            pipeline = cross_validation_best(pipes, x_array, y_array)
            pipeline.fit(x_array, y_array.ravel())
        return pipeline
    except ValueError as e:
        if debug:
            print(e)
        return -1

#===============================================================================
# build_and_train_td ()
#===============================================================================
def build_and_train_td(horizon_param, project_param, regressor_param, ground_truth_param):
    """
    Build TD forecasting models and return forecasts for an horizon specified by the user.
    Arguments:
        horizon_param: The forecasting horizon up to which forecasts will be produced.
        project_param: The project for which the forecasts will be produced.
        regressor_param: The regressor models that will be used to produce forecasts.
        ground_truth_param: If the model will return also ground truth values or not.
    Returns:
        A dictionary containing forecasted values (and ground thruth values if
        ground_truth_param is set to yes) for each intermediate step ahead up
        to the specified horizon.
    """

    # selecting indicators that will be used as model variables
    metrics_td = ['code_smells', 'ncloc', 'complexity', 'duplicated_blocks', 'sqale_index', 'reliability_remediation_effort', 'security_remediation_effort']
    # Select sliding window length
    window_size = 2

    # Read dataset
    try:
        dataset_td = pd.read_csv('data/%s.csv' % project_param, sep=";", usecols=metrics_td)
    except FileNotFoundError as e:
        if debug:
            print(e)
        return -2

    dataset_td['total_principal'] = dataset_td['reliability_remediation_effort'] + dataset_td['security_remediation_effort'] + dataset_td['sqale_index']
    dataset_td = dataset_td.drop(columns=['sqale_index', 'reliability_remediation_effort', 'security_remediation_effort'])

    # Initialise variables
    dict_result = {
        'parameters': {
            'project': project_param,
            'horizon': horizon_param,
            'regressor': regressor_param,
            'ground_truth': ground_truth_param
        }
    }
    list_forecasts = []
    list_ground_truth = []

    # Make forecasts using the Direct approach, i.e. train separate ML models for each forecasting horizon
    for intermediate_horizon in range(1, horizon_param+1):
        if debug:
            print('=========================== Horizon: %s ============================' % intermediate_horizon)

        # Add time-shifted prior and future period
        data = series_to_supervised(dataset_td, n_in=window_size)

        # Append dependend variable column with value equal to total_principal of the target horizon's version
        data['forecasted_total_principal'] = data['total_principal(t)'].shift(-intermediate_horizon)
        data = data.drop(data.index[-intermediate_horizon:])

        # Remove TD as independent variable
        data = data.drop(columns=['total_principal(t-%s)' % (i) for i in range(window_size, 0, -1)]) 

        # Define independent and dependent variables
        x_array = data.iloc[:, data.columns != 'forecasted_total_principal'].values
        y_array = data.iloc[:, data.columns == 'forecasted_total_principal'].values

        # Deploy model
        # Assign version counter
        version_counter = len(dataset_td)+intermediate_horizon
        # Define X to to deploy model for real forecasts
        x_real = series_to_supervised(dataset_td, n_in=window_size, dropnan=False)
        x_real = x_real.drop(columns=['total_principal(t-%s)' % (i) for i in range(window_size, 0, -1)])
        x_real = x_real.iloc[-1, :].values
        x_real = x_real.reshape(1, -1)
        # Make real forecasts
        regressor = create_regressor(regressor_param, x_array, y_array)
        if regressor is -1:
            return -1
        y_pred = regressor.predict(x_real)

        # Fill dataframe with forecasts
        temp_dict = {
            'version': version_counter,
            'value': float(y_pred[0])
        }
        list_forecasts.append(temp_dict)

    # Fill results dictionary with forecasts
    dict_result['forecasts'] = list_forecasts

    # If the model will return also ground truth values
    if ground_truth_param == 'yes':
        # Fill dataframe with ground thruth
        for intermediate_horizon in range(0, len(dataset_td['total_principal'])):
            temp_dict = {
                'version': intermediate_horizon + 1,
                'value': float(dataset_td['total_principal'][intermediate_horizon])
            }
            list_ground_truth.append(temp_dict)
        # Fill results dictionary with ground thruth
        dict_result['ground_truth'] = list_ground_truth

    if debug:
        print(dict_result)

    return dict_result

#===============================================================================
# build_and_train_td_file_level ()
#===============================================================================
def build_and_train_td_file_level(horizon_param, project_param, project_files_param, regressor_param, ground_truth_param):
    """
    Build file-level TD forecasting models and return forecasts for an horizon specified by the user.
    Arguments:
        horizon_param: The forecasting horizon up to which forecasts will be produced.
        project_param: The project for which the forecasts will be produced.
        project_files_param: The number of files for which the forecasts will be produced.
        regressor_param: The regressor models that will be used to produce forecasts.
        ground_truth_param: If the model will return also ground truth values or not.
    Returns:
        A dictionary containing fike-level forecasted values (and ground thruth
        values if ground_truth_param is set to yes) of the selected project, for
        a number of files specified by the user and for each intermediate step
        ahead up to the specified horizon.
    """

    # Read file-level dataset
    try:
        dataset_td_file = pd.read_csv('data/%s_class.csv' % project_param, sep=";")
    except FileNotFoundError as e:
        if debug:
            print(e)
        return -2

    # selecting indicators that will be used as model variables
    metrics_td = ['code_smells', 'ncloc', 'complexity', 'duplicated_blocks', 'total_principal']

    # Select sliding window length
    window_size = 2

    # Compute change proneness and TD change proneness for each file
    files_change_metrics_df = pd.DataFrame()
    for file_id in dataset_td_file['class_id'].unique().tolist():
        # create temporary file dataframe
        temp_file_df = dataset_td_file[dataset_td_file['class_id'] == file_id]

        temp_file_metr_dict = {}
        temp_file_name = temp_file_df.class_name.iloc[0]
        temp_file_metr_dict['file_id'] = file_id
        temp_file_metr_dict['file_name'] = temp_file_name
        temp_file_metr_dict['versions'] = temp_file_df.shape[0]
        temp_file_metr_dict['td_of_last_version'] = temp_file_df.total_principal.iloc[-1]
        temp_file_metr_dict['complexity_of_last_version'] = temp_file_df.complexity.iloc[-1]

        # compute number of changes in LOC across versions of a file
        ncloc_has_changed_list = temp_file_df.ncloc == temp_file_df.ncloc.shift()
        ncloc_has_changed_list = [1 if i==False else 0 for i in ncloc_has_changed_list]
        file_df_changes = sum(ncloc_has_changed_list)
        temp_file_metr_dict['number_of_changes'] = file_df_changes

        # compute LOC Change Proneness of a file
        file_df_cp = (file_df_changes/temp_file_df.shape[0])
        temp_file_metr_dict['change_proneness_(CP)'] = file_df_cp

        # compute number of changes in TD across versions of a file
        td_has_changed_list = temp_file_df.total_principal == temp_file_df.total_principal.shift()
        td_has_changed_list = [1 if i==False else 0 for i in td_has_changed_list]
        file_df_changes_td = sum(td_has_changed_list)
        temp_file_metr_dict['number_of_td_changes'] = file_df_changes_td

        # compute TD Change Proneness of a file
        file_df_cp_td = (file_df_changes_td/temp_file_df.shape[0])
        temp_file_metr_dict['change_proneness_td_(CP-TD)'] = file_df_cp_td

        # compute number of changes in complexity across versions of a file
        complexity_has_changed_list = temp_file_df.complexity == temp_file_df.complexity.shift()
        complexity_has_changed_list = [1 if i==False else 0 for i in complexity_has_changed_list]
        file_df_complexity_changes = sum(complexity_has_changed_list)
        temp_file_metr_dict['number_of_complexity_changes'] = file_df_complexity_changes

        # compute complexity Change Proneness of a file
        file_df_CP_complexity = (file_df_complexity_changes/temp_file_df.shape[0])
        temp_file_metr_dict['change_proneness_complexity_(CP-COMP)'] = file_df_CP_complexity

        # compute average size of changes in LOC across versions of a file
        ncloc_changes_volume_list = temp_file_df['ncloc'].diff(periods=1)
        ncloc_changes_volume_list.fillna(0,inplace=True)
        file_df_expected_changes = sum(ncloc_changes_volume_list)/(temp_file_df.shape[0]-1)
        temp_file_metr_dict['expected_size_change_(ED-LOC)'] = file_df_expected_changes

        # compute average size of changes in TD across versions of a file
        td_changes_volume_list = temp_file_df['total_principal'].diff(periods=1)
        td_changes_volume_list.fillna(0,inplace=True)
        file_df_expected_td_changes = sum(td_changes_volume_list)/(temp_file_df.shape[0]-1)
        temp_file_metr_dict['expected_td_change_(ED-TD)'] = file_df_expected_td_changes

        # compute average size of changes in complexity across versions of a file
        complexity_changes_volume_list = temp_file_df['complexity'].diff(periods=1)
        complexity_changes_volume_list.fillna(0,inplace=True)
        file_df_expected_complexity_changes = sum(complexity_changes_volume_list)/(temp_file_df.shape[0]-1)
        temp_file_metr_dict['expected_complexity_change_(ED-COMP)'] = file_df_expected_complexity_changes

        temp_file_metr_df = pd.DataFrame.from_records([temp_file_metr_dict], index='file_id', columns=temp_file_metr_dict.keys())
        files_change_metrics_df = files_change_metrics_df.append(temp_file_metr_df)

    # Sort files by Change Proneness (CP)
    files_change_metrics_df.sort_values(by=['change_proneness_(CP)'], ascending=False, inplace=True)

    # Keep only first n files, where n = project_files_param
    files_change_metrics_df = files_change_metrics_df.head(project_files_param)

    # Initialise variables
    dict_result = {
        'parameters': {
            'project': project_param,
            'files': project_files_param,
            'horizon': horizon_param,
            'regressor': regressor_param,
            'ground_truth': ground_truth_param
        }
    }
    list_forecasts = []
    list_metrics = []
    list_ground_truth = []

    # Compute forecasts for each file
    for index, file_instance in files_change_metrics_df.iterrows():
        if debug:
            print('=========================== File: %s ============================' % file_instance['file_name'])
        temp_file_df = dataset_td_file.loc[dataset_td_file['class_id'] == index]
        temp_file_df.reset_index(inplace=True, drop=True)

        temp_dataset_td_file = temp_file_df[metrics_td]

        # Fill list with metrics of files
        temp_metrics_dict = {
            file_instance['file_name']: pd.DataFrame(file_instance).T.to_dict('records')[0]
        }
        list_metrics.append(temp_metrics_dict)

        temp_list_forecasts = []

        # Make forecasts using the Direct approach, i.e. train separate ML models for each forecasting horizon
        for intermediate_horizon in range(1, horizon_param+1):
            if debug:
                print('=========================== Horizon: %s ============================' % intermediate_horizon)

            # Add time-shifted prior and future period
            data = series_to_supervised(temp_dataset_td_file, n_in=window_size)

            # Append dependend variable column with value equal to total_principal of the target horizon's version
            data['forecasted_total_principal'] = data['total_principal(t)'].shift(-intermediate_horizon)
            data = data.drop(data.index[-intermediate_horizon:])

            # Remove TD as independent variable
            data = data.drop(columns=['total_principal(t-%s)' % (i) for i in range(window_size, 0, -1)])

            # Define independent and dependent variables
            x_array = data.iloc[:, data.columns != 'forecasted_total_principal'].values
            y_array = data.iloc[:, data.columns == 'forecasted_total_principal'].values

            # Deploy model
            # Assign version counter
            version_counter = len(temp_dataset_td_file)+intermediate_horizon
            # Define X to to deploy model for real forecasts
            x_real = series_to_supervised(temp_dataset_td_file, n_in=window_size, dropnan=False)
            x_real = x_real.drop(columns=['total_principal(t-%s)' % (i) for i in range(window_size, 0, -1)])
            x_real = x_real.iloc[-1, :].values
            x_real = x_real.reshape(1, -1)
            # Make real forecasts
            regressor = create_regressor(regressor_param, x_array, y_array)
            if regressor is -1:
                return -1
            y_pred = regressor.predict(x_real)

            # Fill list with forecasts
            temp_forecasts_dict = {
                'version': version_counter,
                'value': float(y_pred[0])
            }
            temp_list_forecasts.append(temp_forecasts_dict)

        # Fill list with forecasts
        temp_file_forecasts_dict = {
            file_instance['file_name']: temp_list_forecasts
        }
        list_forecasts.append(temp_file_forecasts_dict)

        # If the model will return also ground truth values
        if ground_truth_param == 'yes':
            temp_list_ground_truth = []
            # Fill dataframe with ground thruth
            for intermediate_horizon in range(0, len(temp_dataset_td_file['total_principal'])):
                temp_ground_truth_dict = {
                    'version': intermediate_horizon + 1,
                    'value': float(temp_dataset_td_file['total_principal'][intermediate_horizon])
                }
                temp_list_ground_truth.append(temp_ground_truth_dict)
            # Fill list with files
            temp_ground_truth_dict = {
                file_instance['file_name']: temp_list_ground_truth
            }
            list_ground_truth.append(temp_ground_truth_dict)

    # Fill results dictionary with change proneness and TD change proneness for each file
    dict_result['change_metrics'] = list_metrics

    # Fill results dictionary with forecasts for each file
    dict_result['forecasts'] = list_forecasts

    # If the model will return also ground truth values
    if ground_truth_param == 'yes':
        # Fill results dictionary with ground thruth
        dict_result['ground_truth'] = list_ground_truth

    if debug:
        print(dict_result)

    return dict_result
