package util;

import java.util.ArrayList;

public class SyncArrayList<E> {
	
	private ArrayList<E> al = new ArrayList<E>();
	private boolean locked = false;
	
	public synchronized void add (E e) {
		if (!locked) {
			al.add(e);
		}
	}
	public synchronized void remove(E e) {
		if (!locked) {
			al.remove(e);
		}
	}
	//one shot thing
	public synchronized void setLocked() {
		locked = true;
	}
	public synchronized boolean getLocked() {
		return locked;
	}
	
	public synchronized ArrayList<E> getLockedArray() {
		if (!locked) {
			return new ArrayList<E>();
		}
		return al;
	}

}
