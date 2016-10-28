/*
 
 Copyright 2015 HJOW

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 */

package hjow.libreference.proxy;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import hjow.libreference.HgCell;
import hjow.libreference.HgRow;

public class PRow implements HgRow
{
	private static final long serialVersionUID = 5870863036417807516L;
	protected List<HgCell> cells = new Vector<HgCell>();
	
	public PRow()
	{
		
	}
	
	public PRow(List<HgCell> cells)
	{
		super();
		this.cells = cells;
	}


	@Override
	public String help()
	{
		return null;
	}

	@Override
	public boolean add(HgCell e)
	{
		return cells.add(e);
	}

	@Override
	public void add(int index, HgCell element)
	{
		cells.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends HgCell> c)
	{
		return cells.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends HgCell> c)
	{
		return cells.addAll(index, c);
	}

	@Override
	public void clear()
	{
		cells.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		return cells.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return cells.containsAll(c);
	}

	@Override
	public HgCell get(int index)
	{
		return cells.get(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return cells.indexOf(o);
	}

	@Override
	public boolean isEmpty()
	{
		return cells.isEmpty();
	}

	@Override
	public Iterator<HgCell> iterator()
	{
		return cells.iterator();
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return cells.lastIndexOf(o);
	}

	@Override
	public ListIterator<HgCell> listIterator()
	{
		return cells.listIterator();
	}

	@Override
	public ListIterator<HgCell> listIterator(int index)
	{
		return cells.listIterator(index);
	}

	@Override
	public boolean remove(Object o)
	{
		return cells.remove(o);
	}

	@Override
	public HgCell remove(int index)
	{
		return cells.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return cells.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return cells.retainAll(c);
	}

	@Override
	public HgCell set(int index, HgCell element)
	{
		return cells.set(index, element);
	}

	@Override
	public int size()
	{
		return cells.size();
	}

	@Override
	public List<HgCell> subList(int fromIndex, int toIndex)
	{
		return cells.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray()
	{
		return cells.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return cells.toArray(a);
	}

	@Override
	public HgCell createCell(int i)
	{
		HgCell newCell = new PCell();
		cells.add(newCell);
		return newCell;
	}

	public List<HgCell> getCells()
	{
		return cells;
	}

	public void setCells(List<HgCell> cells)
	{
		this.cells = cells;
	}
	@Override
	public void noMoreUse()
	{
		
	}
	@Override
	public boolean isAlive()
	{
		return true;
	}
}
