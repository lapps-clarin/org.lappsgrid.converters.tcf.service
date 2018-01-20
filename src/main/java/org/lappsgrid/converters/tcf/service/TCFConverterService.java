package org.lappsgrid.converters.tcf.service;

import org.lappsgrid.api.WebService;
import org.lappsgrid.converter.tcf.TCFConverter;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.metadata.ServiceMetadataBuilder;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.lappsgrid.discriminator.Discriminators.*;

/**
 * @author Keith Suderman
 */
public class TCFConverterService implements WebService
{
	private String metadata;
	private TCFConverter converter;

	private Logger logger;

	public TCFConverterService()
	{
		logger = LoggerFactory.getLogger(TCFConverter.class);
		logger.info("Creating service.");

		metadata = null;
		converter = new TCFConverter();
	}

	@Override
	public String execute(String input)
	{
		logger.info("servicing request.");
		Data data = Serializer.parse(input, Data.class);
		if (Uri.ERROR.equals(data.getDiscriminator()))
		{
			logger.warn("Received an error input.");
			return input;
		}
		if (!Uri.TCF.equals(data.getDiscriminator()))
		{
			logger.warn("Received an invalid discriminator: {}", data.getDiscriminator());
			return new Data(Uri.ERROR, "Unsupported input format").asJson();
		}
//		return converter.convertString(data.getPayload().toString()).asJson();
		Data result = null;
		try {
			logger.debug("Attempting to call converter.convertString");
			result = converter.convertString(data.getPayload().toString());
		}
		catch (Throwable t) {
			logger.error("Caught a Throwable.", t);
			StringWriter writer = new StringWriter();
			PrintWriter out = new PrintWriter(writer);
			out.println("Caught an exception converting a TCF string.");
			out.println(t.getMessage());
			t.printStackTrace(out);
			result = new Data(Uri.ERROR, writer.toString());
		}
		logger.debug("Return some json.");
		return result.asJson();
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
