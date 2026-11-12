package repositorio;

import java.util.ArrayList;
import java.util.HashMap;

// Repositorio genérico en memoria que sirve para cualquier tipo T.
// ArrayList: guarda los elementos en orden para poder listarlos.
// HashMap: permite buscar rápido un elemento por su código.
public class RepositorioGenerico<T> {

    private ArrayList<T> lista = new ArrayList<>();
    private HashMap<String, T> mapa = new HashMap<>();

    public void agregar(String codigo, T elemento) {
        if (mapa.containsKey(codigo)) {
            throw new IllegalArgumentException("Ya existe un registro con el código " + codigo);
        }
        lista.add(elemento);
        mapa.put(codigo, elemento);
    }

    public T buscar(String codigo) {
        return mapa.get(codigo);
    }

    public boolean existe(String codigo) {
        return mapa.containsKey(codigo);
    }

    public void eliminar(String codigo) {
        T elemento = mapa.remove(codigo);
        if (elemento != null) {
            lista.remove(elemento);
        }
    }

    // Devuelve una copia para que nadie modifique la lista interna sin querer.
    public ArrayList<T> listar() {
        return new ArrayList<>(lista);
    }
}
