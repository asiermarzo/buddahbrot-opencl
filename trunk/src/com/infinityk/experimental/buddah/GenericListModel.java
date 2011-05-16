/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityk.experimental.buddah;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractListModel;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLDevice;

/**
 *
 * @author Asier
 */
public class GenericListModel<E> extends AbstractListModel {

    private final List<E> elements;

    public GenericListModel(int size) {
        elements = new ArrayList<E>(size);
    }

    public GenericListModel() {
        elements = new ArrayList<E>();
    }

    public GenericListModel(List<E> elements) {
        this.elements = elements;
    }

    public List<E> getElements() {
        return elements;
    }

    public synchronized void clear() {
        int size = elements.size();
        if (size > 0) {
            elements.clear();
            fireIntervalRemoved(this, 0, size - 1);
        }
    }

    public synchronized void add(E element) {

        int size = elements.size();
        elements.add(element);
        fireIntervalAdded(this, size, size);

    }

    public synchronized void add( int index,E element) {
        elements.add(index, element);
        fireIntervalAdded(this, index, index);

    }

    public synchronized void delete(int index) {

        elements.remove(index);
        fireIntervalRemoved(this, index, index);

    }

    public synchronized void delete(E element) {
        int index = elements.indexOf(element);
        if (index != -1) {
            elements.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }

    public synchronized void delete(int[] indices) {

        Arrays.sort(indices);
        int offset = 0;
        for (int i : indices) {
            elements.remove(i - offset);
            fireIntervalRemoved(this, i - offset, i - offset);
            offset++;
        }

    }

    public synchronized void update(E element) {

        int index = elements.indexOf(element);
        if (index != -1) {
            fireContentsChanged(this, index, index);
        }

    }

    public void update(int index) {
        fireContentsChanged(this, index, index);
    }

    public E getAt(int index) {
        return elements.get(index);
    }

    @Override
    public int getSize() {
        return elements.size();
    }

    @Override
    public Object getElementAt(int index) {
        E element = elements.get(index);
        if (element instanceof CLDevice){
            CLDevice d = (CLDevice)element;
            return d.getInfoString(CL10.CL_DEVICE_NAME);
        }else{
            return element.toString();
        }
    }

    public synchronized void copyFrom(GenericListModel<E> other) {
        clear();
        List<E> otherElements = other.getElements();
        int size = otherElements.size();
        if (size > 0) {
            elements.addAll(otherElements);
            fireIntervalAdded(this, 0, size - 1);
        }

    }

    public synchronized void upElement(E element) {

        int index = elements.indexOf(element);
        if (index > 0) {
            E aux = elements.get(index - 1);
            elements.set(index - 1, element);
            elements.set(index, aux);
            fireContentsChanged(this, index - 1, index);
        }

    }

    public synchronized void upElement(int index) {

        E element = getAt(index);
        if (index > 0) {
            E aux = elements.get(index - 1);
            elements.set(index - 1, element);
            elements.set(index, aux);
            fireContentsChanged(this, index - 1, index);
        }

    }

    public synchronized void downElement(E element) {

        int index = elements.indexOf(element);
        if (index < elements.size() - 1) {
            E aux = elements.get(index + 1);
            elements.set(index + 1, element);
            elements.set(index, aux);
            fireContentsChanged(this, index, index + 1);
        }

    }

    public synchronized void downElement(int index) {

        E element = getAt(index);
        if (index < elements.size() - 1) {
            E aux = elements.get(index + 1);
            elements.set(index + 1, element);
            elements.set(index, aux);
            fireContentsChanged(this, index, index + 1);
        }

    }
    
}
