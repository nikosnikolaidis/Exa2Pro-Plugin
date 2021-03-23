FROM continuumio/miniconda:latest

WORKDIR /home/td-forecaster

COPY environment.yml ./
COPY data ./data
COPY td_forecaster_service.py ./
COPY model_training.py ./
COPY utils.py ./

RUN conda env create -f environment.yml
RUN echo "source activate exa2pro_td_forecaster" > ~/.bashrc
ENV PATH /opt/conda/envs/exa2pro_td_forecaster/bin:$PATH

EXPOSE 5001