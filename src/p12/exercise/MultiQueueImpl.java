package p12.exercise;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q>{

    Map<Q,List<T>> queuesMap;
    Set<Q> availableQueue;
    MultiQueueImpl(){
        queuesMap = new HashMap<>();
        availableQueue = new HashSet<Q>();
    }

    @Override
    public Set<Q> availableQueues() {
        /*Creation of availableQueues copy and returning it*/
        Set<Q> copyToReturn = new HashSet<Q>(); 
        for(var queue : availableQueue){
            copyToReturn.add(queue);
        }
        return copyToReturn;
    }

    private void doesNotExistQueue(Q queue){
        if(availableQueue.contains(queue)){
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void openNewQueue(Q queue) {
        doesNotExistQueue(queue);
        availableQueue.add(queue);
        queuesMap.put(queue, new LinkedList<T>());
    }

    @Override
    public boolean isQueueEmpty(Q queue) {
        doesNotExistQueue(queue);
        return queuesMap.get(queue).isEmpty();
    }

    private void doesElementExistInQueue(T elem, Q queue) {
        doesNotExistQueue(queue);
        if(queuesMap.get(queue).contains(elem)){
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void enqueue(T elem, Q queue) {
        doesElementExistInQueue(elem, queue);
        queuesMap.get(queue).add(elem);
    }


    @Override
    public T dequeue(Q queue) {
        if(queuesMap.get(queue).isEmpty()){
            throw new IllegalStateException();
        }
        var dequeuedElement = queuesMap.get(queue).get(0);
        queuesMap.get(queue).remove(0);
        return dequeuedElement;
    }

    @Override
    public Map<Q, T> dequeueOneFromAllQueues() {
        Map<Q,T> mapQueueElement = new HashMap<>();
        for(var queue : availableQueue){
            try{
                mapQueueElement.put(queue, dequeue(queue));
            }catch (IllegalStateException e){
                System.out.println("La queue " + queue + " non e' vuota");
            }
        }
        return mapQueueElement;
    }

    @Override
    public Set<T> allEnqueuedElements() {
        Set<T> enqueuedElements = new HashSet<>();
        for(var queue : availableQueue){
            for(var elem : queuesMap.get(queue)){
                enqueuedElements.add(elem);
            }
        }
        return enqueuedElements;
    }

    @Override
    public List<T> dequeueAllFromQueue(Q queue) {
        doesNotExistQueue(queue);
        List<T> dequeuedElements = new LinkedList<>();
        var queueIterator = queuesMap.get(queue).iterator(); 
        while (queueIterator.hasNext()) {
            dequeuedElements.add(queueIterator.next());
            queueIterator.remove();
        }
        return dequeuedElements;
    }

    @Override
    public void closeQueueAndReallocate(Q queue) {
        doesNotExistQueue(queue);
        if(availableQueue.size() == 1){
            throw new IllegalStateException();
        }
        /*
         * Controllo la prima queue di quelle disponibili, se non è quella che sto riallocando
         * rialloco su quella, altrimenti prendo la successiva
         */
        var finalQueue = availableQueue.iterator().next();
        finalQueue = finalQueue != queue? finalQueue : availableQueue.iterator().next();
        for(var elem : dequeueAllFromQueue(queue)){
            queuesMap.get(finalQueue).add(elem);
        } 
        queuesMap.remove(queue);
        availableQueue.remove(queue);
    }

}
