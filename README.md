## Fading Signal Propagation Model

This application of Cytoscape 3.x provides functions of visualizing and computing influence spreading to a directed network where edges are activation on inhibition.

This application is based on a simple model, Fading Signal Propagation Model (FSPM), described in manual. It evaluates how changing the state of some proteins and complexes affects other proteins and complexes through the network.

This model can be useful to have simply an idea of the functioning of large network and allow identifying the main sets of actors and the contribution of different paths to influence.

By comparing to observations, it questions the relevance of the structure and the feature of edges in the network. So, it help to prepare more accurate model, Boolean for example.

Few parameters are to be adjusted:
- influence reach, number of edges beyond that the influence is neglected ;<br>
- the round threshold to compare to measures.

This model is a continuation of a qualitative analysis which help one's intuition. The core functions are computing influence and comparing to observations. They are completed by inputting parameters, calibrating model and facilitating use (simulation, list paths ...). For more details, see manual.

daniel.rovera@gmail.com
