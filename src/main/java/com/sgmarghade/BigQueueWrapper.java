package com.sgmarghade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansoft.bigqueue.BigQueueImpl;
import com.leansoft.bigqueue.IBigQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by swapnil on 25/05/15.
 */
public class BigQueueWrapper<T> {
    private IBigQueue bigQueue;
    private Class<T> classType;
    private ObjectMapper mapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    public BigQueueWrapper(String queueDir, String queueName, ObjectMapper mapper, Class<T> classType) throws IOException {
        this.bigQueue = new BigQueueImpl(queueDir,queueName);
        this.classType = classType;
        this.mapper = mapper;
    }


    public boolean isEmpty() {
        return bigQueue.isEmpty();
    }

    public Boolean enqueue(T doc){
        lock.lock();

        try {
            Boolean isOffered = false;
            try {
                bigQueue.enqueue(mapper.writeValueAsBytes(doc));
                isOffered = true;
                signalAll();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            return isOffered;
        } finally {
            lock.unlock();
        }
    }



    /**
     * take() is blocking operation. Thread will be blocked till some data arrives in queue. Use calling dequeue directly if u dont want blocking operation.
     * @return Class<T>
     * @throws InterruptedException
     * @throws IOException
     */
    public T take() throws InterruptedException, IOException {
        lock.lock();
        try {
            T wrapper = this.dequeue();
            if (wrapper == null) {
                while ((wrapper = this.dequeue()) == null) {
                    logger.debug("Waiting for take condition");
                    await();
                }
            }
            return wrapper;
        } finally {
            lock.unlock();
        }
    }



    /**
     * This method will dequeue top element from bigqueue convert to your data type
     * @return Class<T>
     * @throws IOException
     */

    public T dequeue() throws IOException {
        T data = null;

            byte[] dequeueData = bigQueue.dequeue();
            if (dequeueData != null && dequeueData.length != 0) {
                data = mapper.readValue(dequeueData, classType);
            }
        return data;
    }

    /**
     *
     * @throws IOException
     */
    public void removeAll() throws IOException {
        bigQueue.removeAll();
    }


    /**
     *
     * @throws IOException
     */
    public void gc() throws IOException {
        bigQueue.gc();
    }


    public void flush() {
        bigQueue.flush();
    }

    public long size() {
        return bigQueue.size();
    }

    /**
     *
     * @throws IOException
     */
    public void close() throws IOException {
        bigQueue.close();
    }

    protected void await() throws InterruptedException {
        notEmpty.await();
    }

    protected void signalAll() {
        notEmpty.signalAll();
    }

    public void drainTo(List<T> list, int size) throws IOException {
        for(int i =0; i< size; i++){
            T data = dequeue();
            if(data != null) {
                list.add(data);
            }
        }
    }
}
