# big_qeue_wrapper
Wrapper over big queue.

Big Queue wrapper provides blocking functionality with take() function. 
take() :-   Thread will be blocked till data is available in the queue. 
drainTo(list,size) :- Will give non null elements in list, max limit = size. 
