package online.motohub.interfaces;


public interface ResponseTransformedCallback<V, T> {

    public V transform(T data);

}
