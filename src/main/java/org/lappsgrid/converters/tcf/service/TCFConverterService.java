package org.lappsgrid.converters.tcf.service;

import org.lappsgrid.api.WebService;
import org.lappsgrid.converter.tcf.TCFConverter;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.metadata.ServiceMetadataBuilder;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import static org.lappsgrid.discriminator.Discriminators.*;

/**
 * @author Keith Suderman
 */
public class TCFConverterService implements WebService
{
	private String metadata;
	private TCFConverter converter;

	public TCFConverterService()
	{
		metadata = null;
		converter = new TCFConverter();
	}

	@Override
	public String execute(String input)
	{
		Data data = Serializer.parse(input, Data.class);
		if (Uri.ERROR.equals(data.getDiscriminator()))
		{
			return input;
		}
		if (!Uri.TCF.equals(data.getDiscriminator()))
		{
			return new Data(Uri.ERROR, "Unsupported input format").asJson();
		}
		return converter.convertString(data.getPayload().toString()).asJson();
	}

	@Override
	public String getMetadata()
	{
		if (metadata == null)
		{
			ServiceMetadata md = new ServiceMetadataBuilder()
					.name("TCF Converter")
					.vendor("http://www.lappsgrid.org")
					.description("Converts TCF into LIF")
					.license("The TCF to LIF converter is available under the `Apache 2.0 <https://www.apache.org/licenses/LICENSE-2.0>`_ license.")
					.requireFormat(Uri.TCF)
					.produceFormat(Uri.LIF)
					.build();
			metadata = new Data(Uri.META, md).asPrettyJson();
		}
		return metadata;
	}
}
