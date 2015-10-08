package org.omnifaces.utils.collection;

import static java.util.Collections.unmodifiableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An immutable list that represents a subset of a larger sequence of results. This list maintains the position of this list within the larger
 * sequence and an estimation of the total number of results in this larger sequence.
 *
 * The position of a single element of this list in the full sequence can be determined by taking the index of the element within this list and adding
 * the offset of this list in the sequence to it. So if an item is at index <code>i</code>, this item is at position <code>i + list.getOffset()</code>
 * in the full sequence.
 *
 * @param <E>
 *            the type of element the list should contain
 */
public class PartialResultList<E> implements List<E> {

	public static final int UNKNOWN_NUMBER_OF_RESULTS = -1;

	private List<E> wrappedList;

	private int offset;
	private int estimatedTotalNumberOfResults;

	public PartialResultList(List<E> wrappedList, int offset, int estimatedTotalNumberOfResults) {
		// Delegate to unmodifiableList to avoid having to implement unmodifiable iterators, etc.
		this.wrappedList = unmodifiableList(wrappedList);
		this.offset = offset;
		this.estimatedTotalNumberOfResults = estimatedTotalNumberOfResults;
	}

	@Override
	public int size() {
		return wrappedList.size();
	}

	@Override
	public boolean isEmpty() {
		return wrappedList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return wrappedList.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return wrappedList.iterator();
	}

	@Override
	public Object[] toArray() {
		return wrappedList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return wrappedList.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return wrappedList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return wrappedList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return wrappedList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return wrappedList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return wrappedList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return wrappedList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return wrappedList.retainAll(c);
	}

	@Override
	public void clear() {
		wrappedList.clear();
	}

	@Override
	public E get(int index) {
		return wrappedList.get(index);
	}

	@Override
	public E set(int index, E element) {
		return wrappedList.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		wrappedList.add(index, element);
	}

	@Override
	public E remove(int index) {
		return wrappedList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return wrappedList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return wrappedList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return wrappedList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return wrappedList.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return wrappedList.subList(fromIndex, toIndex);
	}

	/**
	 * Returns the offset of this list in the full sequence.
	 *
	 * @return the offset of this list in the full sequence
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns the estimated total number of results in the full sequence.
	 *
	 * @return the total number of results in the full sequence or -1 if no estimate can be given
	 */
	public int getEstimatedTotalNumberOfResults() {
		return estimatedTotalNumberOfResults;
	}

}