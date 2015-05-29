# big_qeue_wrapper
## Wrapper over big queue. [https://github.com/bulldog2011/bigqueue](https://github.com/bulldog2011/bigqueue)

Big Queue wrapper provides blocking functionality with take() function. 

take() :-   Thread will be blocked till data is available in the queue. 

drainTo(list,size) :- Will give non null elements in list, max limit = size.

## Usage :-

BigQueueWrapper wrapper = new BigQueueWrapper("/Users/swapnil", "test", new ObjectMapper(), MySerialisableClass.class);

wrapper.enqueue(new MySerialisableClass());



 
