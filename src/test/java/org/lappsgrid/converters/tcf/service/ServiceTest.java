/*
 * Copyright (c) 2017 The Language Applications Grid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.lappsgrid.converters.tcf.service;

import org.junit.*;
import org.lappsgrid.api.WebService;
import org.lappsgrid.converter.tcf.TCFConverter;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.lappsgrid.discriminator.Discriminators.*;

/**
 * @author Keith Suderman
 */
public class ServiceTest
{
	public ServiceTest()
	{

	}

	@Test
	public void testConvertString()
	{
		InputStream stream = this.getClass().getResourceAsStream("/karen-flew.xml");
		assertNotNull(stream);

		TCFConverter converter = new TCFConverter();
		Data data = converter.convert(new InputStreamReader(stream));
		assertNotNull(data);
		assertEquals(Uri.LIF, data.getDiscriminator());
		validateContainer((Container) data.getPayload());
	}

	@Test
	public void testExecute() {
		WebService service = new TCFConverterService();
		InputStream stream = this.getClass().getResourceAsStream("/karen-flew.xml");
		assertNotNull(stream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String tcf = reader.lines().collect(Collectors.joining("\n"));
		Data data = new Data(Uri.TCF, tcf);

		String json = service.execute(data.asJson());
		data = Serializer.parse(json, DataContainer.class);

		assertEquals(Uri.LIF, data.getDiscriminator());
		validateContainer((Container)data.getPayload());
		System.out.println(data.asPrettyJson());
	}

	@Test
	public void testKarenTcfLif() {
		WebService service = new TCFConverterService();
		InputStream stream = this.getClass().getResourceAsStream("/karen-tcf.lif");
		assertNotNull(stream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String tcf = reader.lines().collect(Collectors.joining("\n"));
		String json = service.execute(tcf);
		Data data = Serializer.parse(json, Data.class);
		assertEquals(Uri.LIF, data.getDiscriminator());
	}

	@Ignore
	public void validateInput() {
		InputStream stream = this.getClass().getResourceAsStream("/karen-tcf.lif");
		String tcf = null;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			tcf = reader.lines().collect(Collectors.joining("\n"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			assert false;
		}
		Data data = Serializer.parse(tcf, Data.class);
		System.out.println(data.getPayload().toString());
	}

	protected void validateContainer(Container container)
	{
		assertNotNull(container);

		//System.out.println(container.getViews().size());
		assertEquals(2, container.getViews().size());

		List<View> views = container.findViewsThatContain(Uri.TOKEN);
		assertEquals(1, views.size());
		assertEquals(6, views.get(0).getAnnotations().size());

		views = container.findViewsThatContain(Uri.SENTENCE);
		assertEquals(1, views.size());
		assertEquals(1, views.get(0).getAnnotations().size());
	}
}
