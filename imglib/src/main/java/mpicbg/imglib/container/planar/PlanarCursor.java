/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.container.planar;

import mpicbg.imglib.container.AbstractImgCursor;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.NativeType;

/**
 * Basic Iterator for {@link PlanarContainer PlanarContainers}
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class PlanarCursor< T extends NativeType< T > > extends AbstractImgCursor< T > implements PlanarLocation
{
	protected final T type;
	protected final PlanarContainer< T, ? > container;

	protected final int lastIndex, lastSliceIndex;
	protected int sliceIndex;
	protected boolean hasNext;

	public PlanarCursor( final PlanarContainer< T, ? > container )
	{
		super( container.numDimensions() );

		this.container = container;
		this.type = container.createLinkedType();
		lastIndex = container.dim[ 0 ] * container.dim[ 1 ] - 1;
		lastSliceIndex = container.getSlices() - 1;

		reset();
	}

	@Override
	public T get() { return type; }

	/**
	 * Note: This test is fragile in a sense that it returns true for elements
	 * after the last element as well.
	 * 
	 * @return false for the last element 
	 */
	@Override
	public boolean hasNext() { return hasNext; }

	@Override
	public void fwd()
	{
		type.incIndex();

		final int i = type.getIndex();
		
		if ( i < lastIndex )
			return;
		else if ( i == lastIndex )
			hasNext = sliceIndex < lastSliceIndex;
		else
		{
			++sliceIndex;
			type.updateIndex( 0 );
			type.updateContainer( this );
		}
	}

	@Override
	public void reset()
	{
		sliceIndex = 0;
		type.updateIndex( -1 );
		type.updateContainer( this );
		hasNext = true;
	}

	@Override
	public String toString() { return type.toString(); }

	@Override
	public Img<T> getImg() { return container; }

	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getLongPosition( d );
	}

	@Override
	public long getLongPosition( final int dim )
	{
		if ( dim == 0 )
			return type.getIndex() % container.dim[ 0 ];
		else if ( dim == 1 )
			return type.getIndex() / container.dim[ 0 ];
		else
		{
			// adapted from IntervalIndexer
			int step = 1;
			for ( int d = 2; d < dim; ++d )
				step *= container.dim[ d ];
			return ( sliceIndex / step ) % container.dim[ dim ];			               
		}
	}

	@Override
	public int getCurrentPlane() { return sliceIndex; }
}
