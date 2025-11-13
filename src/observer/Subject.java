package observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject implements Observable {

    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(NotificacaoProduto msg) {
        for (Observer o : observers) {
            o.update(msg);
        }
    }

    public void notificarObservers() {
        String mensagem = getClass().getSimpleName() + " atualizado.";
        notifyObservers(new NotificacaoProduto(mensagem));
    }

    public List<Observer> getObservers() {
        return observers;
    }
}
