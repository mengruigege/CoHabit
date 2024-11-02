import java.util.ArrayList;

public interface Listable<T> {
    ArrayList<T> getList();
    boolean addItem(T item);
    boolean removeItem(T item);
}
