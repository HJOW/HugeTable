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

import hjow.libreference.HgRow;
import hjow.libreference.HgSheet;

public class PSheet implements HgSheet
{
	private static final long serialVersionUID = -2212861964931308120L;
	protected List<HgRow> rows = new Vector<HgRow>();
	protected String name = "";
	
	public PSheet()
	{
		
	}

	public PSheet(List<HgRow> rows)
	{
		super();
		this.rows = rows;
	}

	@Override
	public String help()
	{
		return null;
	}

	@Override
	public boolean add(HgRow e)
	{
		return rows.add(e);
	}

	@Override
	public void add(int index, HgRow element)
	{
		rows.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends HgRow> c)
	{
		return rows.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends HgRow> c)
	{
		return rows.addAll(index, c);
	}

	@Override
	public void clear()
	{
		rows.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		return rows.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return rows.containsAll(c);
	}

	@Override
	public HgRow get(int index)
	{
		return rows.get(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return rows.indexOf(o);
	}

	@Override
	public boolean isEmpty()
	{
		return rows.isEmpty();
	}

	@Override
	public Iterator<HgRow> iterator()
	{
		return rows.iterator();
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return rows.lastIndexOf(o);
	}

	@Override
	public ListIterator<HgRow> listIterator()
	{
		return rows.listIterator();
	}

	@Override
	public ListIterator<HgRow> listIterator(int index)
	{
		return rows.listIterator(index);
	}

	@Override
	public boolean remove(Object o)
	{
		return rows.remove(o);
	}

	@Override
	public HgRow remove(int index)
	{
		return rows.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return rows.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return rows.retainAll(c);
	}

	@Override
	public HgRow set(int index, HgRow element)
	{
		return rows.set(index, element);
	}

	@Override
	public int size()
	{
		return rows.size();
	}

	@Override
	public List<HgRow> subList(int fromIndex, int toIndex)
	{
		return rows.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray()
	{
		return rows.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return rows.toArray(a);
	}

	@Override
	public HgRow createRow(int i)
	{
		HgRow newRow = new PRow();
		rows.add(newRow);
		return newRow;
	}

	public List<HgRow> getRows()
	{
		return rows;
	}

	public void setRows(List<HgRow> rows)
	{
		this.rows = rows;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
