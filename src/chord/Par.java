package chord;
import java.util.Objects;


public class Par<K,V> {
	public final K key;
    public final V value;

    public Par(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey(){
      return key;
    }

    public V getValue(){
      return value;
    }

    public boolean equals(Object o) {
        return o instanceof Par && Objects.equals(key, ((Par<?,?>)o).key) && Objects.equals(value, ((Par<?,?>)o).value);
    }

    public int hashCode() {
        return 31 * Objects.hashCode(key) + Objects.hashCode(value);
    }

    public String toString() {
        return key + "=" + value;
    }
}
