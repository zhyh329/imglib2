package mpicbg.imglib.container.planar;

import mpicbg.imglib.type.NativeType;

public class PlanarLocalizingCursor2D< T extends NativeType< T > > extends PlanarLocalizingCursor< T > 
{
	final protected int maxIndex;
	final int width;
	
	public PlanarLocalizingCursor2D( final PlanarContainer<T, ?> container )
	{
		super( container );
		
		maxIndex = (int)container.numPixels() - 1;
		width = (int)container.dimension( 0 );
	}
	
	@Override
	public boolean hasNext()
	{
		return type.getIndex() < maxIndex;
	}

	@Override
	public void fwd()
	{
		type.incIndex();

		if ( ++position[ 0 ] == width )
		{
			position[ 0 ] = 0;
			++position[ 1 ];
		}
	}
}
