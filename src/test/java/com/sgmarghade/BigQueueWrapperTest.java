package com.sgmarghade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.Executors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by swapnil on 25/05/15.
 */
public class BigQueueWrapperTest {
    private BigQueueWrapper<TestModel> wrapper;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeMethod
    public void setup() throws IOException {
        mapper = mock(ObjectMapper.class);
        String tableName = ""+System.currentTimeMillis()+Math.random();
        wrapper = spy(new BigQueueWrapper<TestModel>(System.getProperty("java.io.tmpdir") + "/bigqueue/",tableName , mapper,
                TestModel.class));
    }


    @AfterMethod
    public void destroy() throws IOException {
        wrapper.close();
    }


    @Test
    public void testBlockCallOnQueueWhenThereIsNoItemInQueue() throws InterruptedException, IOException {
        when(wrapper.dequeue()).thenReturn(null).thenReturn(null).thenReturn(mock(TestModel.class));
        doNothing().when(wrapper).await();
        wrapper.take();
        verify(wrapper, times(1)).await();
    }

    @Test
    void testDoNotBlockIfDataIsAvailable() throws InterruptedException, IOException {
        when(wrapper.dequeue()).thenReturn(mock(TestModel.class));
        wrapper.take();
        verify(wrapper, never()).await();
    }

    @Test
    void testReceiveWaitEventWhenThreadPushedDataBackToQueue() throws InterruptedException, IOException {
        when(wrapper.dequeue()).thenReturn(null).thenReturn(null).thenReturn(mock(TestModel.class));
        when(mapper.writeValueAsBytes(any(TestModel.class))).thenReturn(new byte[] { 2 });

        // Launch thread here to push data to queue after some time..
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    wrapper.enqueue(mock(TestModel.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wrapper.enqueue(mock(TestModel.class));
            }
        });

        wrapper.take();
        verify(wrapper, times(1)).await();
    }

}


