package com.seven10.update_guy.repository;

import java.nio.file.Path;
import java.util.function.Consumer;

public class SpyablePathConsumer implements Consumer<Path>
{

	@Override
	public void accept(Path t)
	{
		// tra-la-lala-la
		return;
	}

}
