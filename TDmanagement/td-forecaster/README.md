# TD Forecaster

## Description

This repository contains the source code of the **TD Forecaster back-end**, which is part of the Technical Debt Management (TDM) tool of the [EXA2PRO Framework](https://exa2pro.eu/). The purpose of the TD Forecaster is to provide predictive forecasts regarding the evolution of the *Technical Debt* quality attribute of an HPC software application, both at system-level and file-level of granularity. The entry point of the TD Forecaster back-end is a RESTful web server that uses the [Flask web framework](https://www.palletsprojects.com/p/flask/) wrapped inside [Waitress](https://docs.pylonsproject.org/projects/waitress/en/stable/), a Python WSGI production-ready server. At a lower level, the server exposes two sub-modules, implemented as individual web services. These services are listed below:
- **System-level TD Forecaster**: This web service is responsible for generating system-level Technical Debt forecasts for a given software application. A system-level TD forecast represents the predicted evolution of the total remediation effort (measured in minutes) to fix all code issues (e.g. code smells, bugs, code duplications, etc.) of a software application, up to a future point specified by the user.
- **File-level TD Forecaster**: This web service is responsible for generating file-level Technical Debt forecasts for the files of a given software application. A file-level TD forecast represents the predicted evolution of the total remediation effort (measured in minutes) to fix all code issues (e.g. code smells, bugs, code duplications, etc.) of a software file, up to a future point specified by the user. In addition to the TD forecast of the file, this service computes and returns also the *change proneness* (i.e., change frequency) and the *expected complexity change* of the file.

The two web services allow the individual and remote invocation of the forecasting models developed for estimating the evolution of TD. This is achieved through the dedicated API exposed by the RESTful web server, which allows the user to perform simple HTTP GET requests to the two web services. Several inputs need to be provided as URL-encoded parameters to these requests. These parameters are listed below:

### System-level TD Forecaster

| Parameter      | Description                                                     | Required | Valid Inputs                                                                                                                                                                                                                                                                                        |
|----------------|-----------------------------------------------------------------|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| *horizon*      | The forecasting horizon up to which forecasts will be produced. | Yes      | An integer in range *[1-N]*, where *N* depends on the volume of data used to train the regressor. Currently there is no upper limit and the service returns an error if this value is set too high.                                                                                                 |
| *project*      | The project ID for which the forecasts will be produced.        | Yes      | Currently the following string values are supported for testing purposes: [*metalwalls_measures*, *kkrnano_measures*, *lattice_qcd_measures*]. Later, this input will be the ID of an actual project retrieved the TDM tool interface.                                                        |
| *regressor*    | The regressor model that will be used to produce forecasts.     | No       | One of the following string values: [*auto*, *mlr*, *lasso*, *ridge*, *svr_linear*, *svr_rbf*, *random_forest*]. Default value is *auto*. If this parameter is omitted, default value is set to *auto* and the service selects automatically the best model based on validation error minimization. |
| *ground_truth* | If the model will return also ground truth values or not.       | No       | One of the following string values: [*yes*, *no*]. Default value is *no*.                                                                                                                                                                                                                           |

The output of the System-level TD Forecaster web service is a JSON file containing the predicted TD values of the selected application. This JSON actually contains i) a status code of the response, ii) a N-size array containing the forecasts, where N is equal to the *horizon* parameter, iii) a recap on the given parameter values, and iv) a message informing the user if the request was fulfilled successfully or not.

### File-level TD Forecaster

| Parameter       | Description                                                                                | Required | Valid Inputs                                                                                                                                                                                                                                                                                        |
|-----------------|--------------------------------------------------------------------------------------------|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| *horizon*       | The forecasting horizon up to which forecasts will be produced.                            | Yes      | An integer in range *[1-N]*, where *N* depends on the volume of data used to train the regressor. Currently there is no upper limit and the service returns an error if this value is set too high.                                                                                                 |
| *project *      | The project ID for which the forecasts will be produced.                                   | Yes      | Currently the following string values are supported for testing purposes: [*metalwalls_measures*, *kkrnano_measures*, *lattice_qcd_measures*]. Later, this input will be the ID of an actual project retrieved the TDM tool interface.                                                              |
| *project_files* | The number of files for which the forecasts and change-proneness metrics will be produced. | Yes      | An integer in range [1-N], where N is the total number of files of the software application.                                                                                                                                                                                                        |
| *regressor*     | The regressor model that will be used to produce forecasts.                                | No       | One of the following string values: [*auto*, *mlr*, *lasso*, *ridge*, *svr_linear*, *svr_rbf*, *random_forest*]. Default value is *auto*. If this parameter is omitted, default value is set to *auto* and the service selects automatically the best model based on validation error minimization. |
| *ground_truth*  | If the model will return also ground truth values or not.                                  | No       | One of the following string values: [*yes*, *no*]. Default value is *no*.                                                                                                                                                                                                                           |

The output of the File-level TD Forecaster web service is a JSON file containing i) a status code of the response, ii) a M-size array of dictionaries (M is equal to the *project_files* parameter), where each dictionary represents a file and contains a N-size array of TD forecasts (N is equal to the *horizon* parameter), iii) a M-size array of dictionaries (M is equal to the *project_files* parameter), where each dictionary represents a file and contains change-proneness analysis metrics, iv) a recap on the given parameter values, and v) a message informing the user if the request was fulfilled successfully or not.

## Installation

In this section, we provide instructions on how the user can build the python Flask server of the TD Forecaster back-end from scratch, using the Anaconda virtual environment. The TD Forecaster is developed to run on Unix and Windows systems with python 3.6.* innstalled. We suggest installing python via the Anaconda distribution as it provides an easy way to create a virtual environment and install dependencies. The configuration steps needed, are described below:

- **Step 1**: Download the latest [Anaconda distribution](https://www.anaconda.com/distribution/) and follow the installation steps described in the [Anaconda documentation](https://docs.anaconda.com/anaconda/install/windows/).
- **Step 2**: Open Anaconda cmd. Running Anaconda cmd activates the base environment. We need to create a specific environment to run TD Forecaster. Create a new python 3.6.4 environment and install the needed libraries by running the following command:
```bash
conda create --name exa2pro_td_forecaster python=3.6.4 numpy=1.18.1 pandas=1.0.3 scikit-learn=0.22.1 waitress=1.4.3 flask=1.1.2 flask-cors=3.0.8 requests=2.23.0
```
This command will result in the creation of a conda environment named *exa2pro_td_forecaster*. In order to activate the new environment, execute the following command:
```bash
conda activate exa2pro_td_forecaster
```
- **Step 3**: To start the server, use the command promt inside the active environment and execute the commands described in section [Run Server](#run-server).
- **alternative step**: To execute the TD Forecaster as a command line application (without using a web server), use the command promt and execute the commands described in section [CLI Usage](#cli-usage).

## Installation using Docker

In this section, we provide instructions on how the user can build a new Docker Image that contains the python Flask app and the Conda environment of the of the TD Forecaster back-end. We highly recommend the users to select this way of installing the EXA2PRO TD Forecaster, as it constitutes the easiest way.

- **Step 1**: Download and install [Docker](https://www.docker.com/)
- **Step 2**: Clone the latest TD Forecaster version and navigate to the home directory. You should see a *DockerFile* and an *environment.yml* file, which contains the Conda environment dependencies. 
- **Step 3**: In the home directory of the TD Forecaster, open cmd and execute the following command:
```bash
sudo docker build -t exa2pro_td_forecaster .
``` 
This command will result in the creation of a Docker Image named *exa2pro_td_forecaster*. In order to create a Docker Container from this image, execute the following command:
```bash
sudo docker run -it --name exa2pro-td-forecaster-test -p 5001:5001 exa2pro_td_forecaster
``` 
This command will generate and run a Docker Container named *exa2pro-td-forecaster-test* in interactive session mode, i.e. it will open a command promt inside the Container. 
- **Step 4**: To start the server, use the command promt inside the running Container and execute the commands described in section [Run Server](#run-server).
- **alternative step**: To execute the TD Forecaster as a command line application (without using a web server), use the command promt and execute the commands described in section [CLI Usage](#cli-usage).

## Run Server

You can run the TD Forecaster server in various modes using Python to run the `td_forecaster_service.py` script:

```
usage: td_forecaster_service.py [-h] [--debug] HOST PORT SERVER_MODE

positional arguments:
  HOST         Server HOST (e.g. "localhost")
  PORT         Server PORT (e.g. "5001")
  SERVER_MODE  builtin, waitress

optional arguments:
  -h, --help   show this help message and exit
  --debug      Run builtin server in debug mode (default: False)
```

`HOST`, `PORT`, and `SERVER_MODE` arguments are **mandatory**. You can set them according to your needs.

### Run built-in Flask server

```
         127.0.0.1:5001
Client <----------------> Flask
```

To start the TD Forecaster using the built-in **Flask** server, use the command promt inside the active Conda or Container environment and execute the following command: 

```bash
python td_forecaster_service.py 0.0.0.0 5001 builtin --debug
```

This command will start the built-in Flask server locally (0.0.0.0) on port 5001.

**Warning**: The built-in Flask mode is useful for development since it has debugging enabled (e.g. in case of error the client gets a full stack trace). However, it is single-threaded. Do NOT use this mode in production!

### Run Waitress server

```
         127.0.0.1:5001
Client <----------------> Waitress <---> Flask
```

To start the TD Forecaster using the **Waitress** server, use the command promt inside the active Conda or Container environment and execute the following command:

```bash
python td_forecaster_service.py 0.0.0.0 5001 waitress
```

This command will start the Waitress server locally (0.0.0.0) on port 5001.

**Warning**: The Waitress mode is higly recommended in real production environments, since it supports scaling and multiple-request handling features.

## Usage

### System-level TD forecast example

Once the server is running, open your web browser and navigate to the following URL:

http://127.0.0.1:5001/TDForecaster/SystemForecasting?horizon=10&project=metalwalls_measures&regressor=ridge&ground_truth=no

You will get a JSON response containing TD forecasts of a sample application (Metalwalls) for an horizon of 10 versions ahead, using the Ridge regressor model.

**Warning**: To produce system-level TD forecasts for a new arbitary application, you need to provide a csv file of TD metrics (extracted using SonarQube) for this particular application. This csv file needs to be placed within the */data* folder and be in a from similar to the *metalwalls_measures.csv* example file.

### File-level TD forecast example

Once the server is running, open your web browser and navigate to the following URL:

http://127.0.0.1:5001/TDForecaster/FileForecasting?horizon=10&project=metalwalls_measures&project_files=10&regressor=lasso&ground_truth=no

You will get a JSON response containing TD forecasts and change proneness metrics of the top 10 files of a sample application (Metalwalls) for an horizon of 10 versions ahead, using the Lasso regressor model.

**Warning**: To produce file-level TD forecasts for a new arbitary application, you need to provide a csv file of TD metrics per file (extracted using SonarQube) for this particular application. This csv file needs to be placed within the */data* folder and be in a from similar to the *metalwalls_measures_class.csv* example file, while its name must contain the *_class* suffix.

## CLI Usage

Alternatively to the web server version, you can also run the TD Forecaster as a command line application using the `td_forecaster_cli.py` script:

```
usage: td_forecaster_cli.py [-h] [--ground_truth] [--write_file] [--debug]
                            GRANULARITY HORIZON PROJECT FILE_NUM REGRESSOR

positional arguments:
  GRANULARITY     The level of granularity for which the forecasts will be
                  produced. Valid inputs: system, file
  HORIZON         The forecasting horizon up to which forecasts will be
                  produced (e.g. "10").
  PROJECT         The name of the project for which the forecasts will be
                  produced (e.g. "metalwalls_measures"). A csv file with the
                  same name (containing comma separated values of TD metrics
                  to be given as input to the model) should exist within the
                  "/data" folder.
  FILE_NUM        The number of files for which the forecasts will be produced
                  during file-level forecasting (e.g. "10").
  REGRESSOR       The regressor models that will be used to produce forecasts.
                  Valid inputs: auto, mlr, lasso, ridge, svr_linear, svr_rbf,
                  random_forest

optional arguments:
  -h, --help      show this help message and exit
  --ground_truth  Return ground truth values in addition to forecasts
                  (default: False)
  --write_file    Write the output to a json file stored in "/output" folder
                  instead of printing to the console. (default: False)
  --debug         Run builtin server in debug mode (default: False)
```

`GRANULARITY`, `HORIZON`, `PROJECT`, `FILE_NUM` and `REGRESSOR` arguments are **mandatory**. You can set them according to your needs.

If `--ground_truth` argument is used, the output will contain also ground truth values in addition to forecasts.

If `--write_file` argument is used, the output will be written to a json file stored in "/output" folder instead of be printed to the console.

### System-level TD forecast example

Once the TD Forecaster is installed and the *exa2pro_td_forecaster* conda environment is activated (either within a running Container or not), use the command promt inside the active environment and execute the following command:

```bash
python td_forecaster_cli.py system 10 metalwalls_measures 10 ridge --ground_truth --write_file
```

You will get a JSON file named *metalwalls_measures_forecasts.json* within the */output* folder containing system-level TD forecasts and ground truth values of a sample application (Metalwalls) for an horizon of 10 versions ahead, using the Ridge regressor model.

**Warning**: To produce system-level TD forecasts for a new arbitary application, you need to provide a csv file of TD metrics (extracted using SonarQube) for this particular application. This csv file needs to be placed within the */data* folder and be in a from similar to the *metalwalls_measures.csv* example file.

### File-level TD forecast example

Once the TD Forecaster is installed and the *exa2pro_td_forecaster* conda environment is activated (either within a running Container or not), use the command promt inside the active environment and execute the following command:

```bash
python td_forecaster_cli.py file 10 metalwalls_measures 10 lasso --ground_truth --write_file
```

You will get a JSON file named *metalwalls_measures_forecasts_class.json* within the */output* folder containing file-level TD forecasts and ground truth values for the top-10 most change-prone files of a sample application (Metalwalls) for an horizon of 10 versions ahead, using the Lasso regressor model.

**Warning**: To produce file-level TD forecasts for a new arbitary application, you need to provide a csv file of TD metrics per file (extracted using SonarQube) for this particular application. This csv file needs to be placed within the */data* folder and be in a from similar to the *metalwalls_measures_class.csv* example file, while its name must contain the *_class* suffix.