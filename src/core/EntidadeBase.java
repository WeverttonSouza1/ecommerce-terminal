package core;

public abstract class EntidadeBase { // classe abstrata // herança
    protected int id;

    public EntidadeBase(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
