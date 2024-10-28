package p12.exercise;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q>{

    private Map<Q,List<T>> queuesMap;
    MultiQueueImpl(){
        queuesMap = new HashMap<>();
    }

    public Set<Q> availableQueues() {
        /*Creation of availableQueues copy and returning it*/
        Set<Q> copyToReturn = new HashSet<Q>(); 
        for(var queue : queuesMap.keySet()){
            copyToReturn.add(queue);
        }
        return copyToReturn;
    }

    private void doesAlreadyExistQueue(final Q queue){
        if(queuesMap.keySet().contains(queue)){
            throw new IllegalArgumentException();
        }
    }
    private void doesNotExistQueue(final Q queue){
        if(!queuesMap.keySet().contains(queue)){
            throw new IllegalArgumentException();
        }
    }

    public void openNewQueue(final Q queue) {
        doesAlreadyExistQueue(queue);
        queuesMap.put(queue, new LinkedList<T>());
    }

    public boolean isQueueEmpty(final Q queue) {
        doesNotExistQueue(queue);
        return queuesMap.get(queue).isEmpty();
    }

    private void doesElementExistInQueue(final T elem, final Q queue) {
        doesNotExistQueue(queue);
        if(queuesMap.get(queue).contains(elem)){
            throw new IllegalArgumentException();
        }
    }

    public void enqueue(final T elem, final Q queue) {
        doesElementExistInQueue(elem, queue);
        queuesMap.get(queue).add(elem);
    }

    public T dequeue(final Q queue) {
        doesNotExistQueue(queue);
        if(queuesMap.get(queue).isEmpty()){
            return null;
        }
        var dequeuedElement = queuesMap.get(queue).get(0);
        queuesMap.get(queue).remove(0);
        return dequeuedElement;
    }

    public Map<Q, T> dequeueOneFromAllQueues() {
        Map<Q,T> mapQueueElement = new HashMap<>();
        for(var queue : queuesMap.keySet()){
            try{
                mapQueueElement.put(queue, dequeue(queue));
            }catch (IllegalStateException e){
                System.out.println("La queue " + queue + " non e' vuota");
            }
        }
        return mapQueueElement;
    }

    public Set<T> allEnqueuedElements() {
        Set<T> enqueuedElements = new HashSet<>();
        for(var queue : queuesMap.keySet()){
            for(var elem : queuesMap.get(queue)){
                enqueuedElements.add(elem);
            }
        }
        return enqueuedElements;
    }

    public List<T> dequeueAllFromQueue(final Q queue) {
        doesNotExistQueue(queue);
        List<T> dequeuedElements = new LinkedList<>();
        var queueIterator = queuesMap.get(queue).iterator(); 
        while (queueIterator.hasNext()) {
            dequeuedElements.add(queueIterator.next());
            queueIterator.remove();
        }
        return dequeuedElements;
    }

    public void closeQueueAndReallocate(final Q queue) {
        doesNotExistQueue(queue);
        if(queuesMap.keySet().size() == 1){
            throw new IllegalStateException();
        }
        /*
         * Controllo la prima queue di quelle disponibili, se non è quella che sto riallocando
         * rialloco su quella, altrimenti prendo la successiva
         */
        var availableQueueIterator = queuesMap.keySet().iterator();
        Q finalQueue = availableQueueIterator.next();
        while(queue.equals(finalQueue) && availableQueueIterator.hasNext()){
            finalQueue = availableQueueIterator.next();
        }
        for(var elem : dequeueAllFromQueue(queue)){
            queuesMap.get(finalQueue).add(elem);
        } 
        queuesMap.remove(queue);
    }

}
