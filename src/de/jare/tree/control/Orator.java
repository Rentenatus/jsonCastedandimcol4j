/* <copyright> 
 * Copyright (c) 2025, Janusch Rentenatus. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * </copyright>
 */
package de.jare.tree.control;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Janusch Rentenatus
 * @param <T>
 */
public class Orator<T> {

    private final List<WeakReference<T>> listenerRefList;

    public Orator() {
        listenerRefList = new ArrayList<>();
    }

    public void addListener(final T listener) {
        synchronized (listenerRefList) {
            if (!contains(listener)) {
                listenerRefList.add(new WeakReference<>(listener));
            }
        }
        removeListener(null);
    }

    public boolean contains(final T listener) {
        synchronized (listenerRefList) {
            for (WeakReference<T> ref : listenerRefList) {
                if (ref.get() == listener) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeListener(final T listener) {
        List<WeakReference<T>> hits = new ArrayList<>();
        synchronized (listenerRefList) {
            for (WeakReference<T> ref : listenerRefList) {
                T candidate = ref.get();
                if (candidate == listener || candidate == null) {
                    hits.add(ref);
                }
            }
            listenerRefList.removeAll(hits);
        }
    }

    public void clear() {
        synchronized (listenerRefList) {
            listenerRefList.clear();
        }
    }

    public void say(Consumer<T> consumer) {
        List<T> hits = new ArrayList<>();
        synchronized (listenerRefList) {
            for (WeakReference<T> ref : listenerRefList) {
                T candidate = ref.get();
                if (candidate != null) {
                    hits.add(candidate);
                }
            }
        }
        for (T hit : hits) {
            consumer.accept(hit);
        }
    }
}
