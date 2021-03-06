/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2018 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.report.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.app.AppSettings;
import com.axelor.db.Model;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.beust.jcommander.internal.Maps;

public class ReportSettings {
	
	private final Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

	public static String FORMAT_PDF = "pdf";
	public static String FORMAT_XLS = "xls";
	public static String FORMAT_XLSX = "xlsx";
	public static String FORMAT_DOC = "doc";
	public static String FORMAT_DOCX = "docx";
	public static String FORMAT_ODS = "ods";
	public static String FORMAT_ODT = "odt";
	public static String FORMAT_HTML = "html";

	protected Map<String, Object> params = Maps.newHashMap();
	
	protected String format = FORMAT_PDF;
	protected String rptdesign;
	protected String outputName;
	protected Model model;
	protected String fileName;
	protected File output;

	private boolean FLAG_ATTACH = false;
    private static final String[] OUTPUT_NAME_SEARCH_LIST = new String[] { "*", "\"", "/", "\\", "?", "%", ":", "|",
            "<", ">" };
    private static final String[] OUTPUT_NAME_REPLACEMENT_LIST = new String[] { "#", "'", "_", "_", "_", "_", "_", "_",
            "_", "_" };

	public ReportSettings(String rptdesign, String outputName)  {
		
		this.rptdesign = rptdesign;
		this.computeOutputName(outputName);
		addDataBaseConnection();
		addAttachmentPath();
		
	}
	
	/**
	 * This method generate the Birt report output.
	 * @return
	 * 		The ReportSettings instance.
	 * @throws AxelorException
	 */
	public ReportSettings generate() throws AxelorException  {
		
		this.computeFileName();

        
        return this;
        
	}
	
	/**
	 * The method get the generated report file link.
	 * @return
	 * 		The generated report file link.
	 */
	public String getFileLink()  {
		
		if(output == null)  {  return null;  }
		
		String fileLink = "ws/files/report/" + output.getName();

        try {
            fileLink += "?name=" + URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
        }

		logger.debug("URL : {}", fileLink);
		
		return fileLink;
		
	}
	
	public String getOutputName()  {
		return outputName;
	}
	
	/**
	 * This method get the generated report file.
	 * @return
	 * 		The generated report file.
	 */
	public File getFile()  {
		return output;
	}
	
	protected void attach() throws FileNotFoundException, IOException  {
		
		if (FLAG_ATTACH && model.getId() != null && output != null) {
			try (InputStream is = new FileInputStream(output)) {
				Beans.get(MetaFiles.class).attach(is, fileName, model);
			}
		}
		
	}
	
	
	protected void computeOutputName(String outputName)  {

		this.outputName = outputName
							.replace("${date}", new DateTime().toString("yyyyMMdd"))
							.replace("${time}", new DateTime().toString("HHmmss"));
        this.outputName = StringUtils.replaceEach(this.outputName, OUTPUT_NAME_SEARCH_LIST,
                OUTPUT_NAME_REPLACEMENT_LIST);
	}
	
	protected void computeFileName()  {
		
		this.fileName = String.format("%s.%s", outputName, format);
		
	}
	
	
	/**
	 * This method can be use to define a specific report output format. The default format is PDF.
	 * @param format
	 * 		The ouput format
	 * 		<p><ul>
	 * 		<li>FORMAT_PDF = "pdf"
	 * 		<li>FORMAT_XLS = "xls"
	 * 		<li>FORMAT_DOC = "doc"
	 * 		<li>FORMAT_HTML = "html"
	 * 		<li>Or any value supported by Birt
	 * 		</ul><p>
	 * @return
	 * 		The ReportSettings instance.
	 */
	public ReportSettings addFormat(String format)  {
		
		if(format != null)  {
			this.format = format;
		}
		
		return this;
		
	}
	
	/**
	 * Method that link the generated report as attachment to the model passed in parameter
	 * @param model
	 * 		An Axelor Model
	 * @return
	 * 		The ReportSettings instance
	 */
	public ReportSettings toAttach(Model model)  {
		
		this.model = Objects.requireNonNull(model);
		FLAG_ATTACH = true;
		
		return this;
		
	}

	/**
	 * This method is use to pass a parameter to the Birt report.
	 * @param param
	 * 		A string key.
	 * @param value
	 * 		An object value. The type of value must be a supported type per the Birt report. 
	 * @return
	 * 		The ReportSettings instance.
	 */
	public ReportSettings addParam(String param, Object value)  {
		
		this.params.put(param, value);
		
		return this;
		
	}
	
	protected ReportSettings addDataBaseConnection()  {
		
		AppSettings appSettings = AppSettings.get();
		
		return this.addParam("DefaultDriver", appSettings.get("db.default.driver"))
		.addParam("DBName", appSettings.get("db.default.url"))
		.addParam("UserName", appSettings.get("db.default.user"))
		.addParam("Password", appSettings.get("db.default.password"));
		
	}
	
	private ReportSettings addAttachmentPath(){
		
		String attachmentPath = AppSettings.get().getPath("file.upload.dir","");
		if(attachmentPath == null){
			return this;
		}
		
		attachmentPath = attachmentPath.endsWith(File.separator) ? attachmentPath : attachmentPath+File.separator;
		
		return this.addParam("AttachmentPath",attachmentPath);
		
	}
	
	public static boolean useIntegratedEngine()  {
		
		AppSettings appsSettings = AppSettings.get();
		
		String useIntegratedEngine = appsSettings.get("axelor.report.use.embedded.engine", "true");
		
		if(useIntegratedEngine.equals("true"))  {  return true;  }
		return false;
		
	}

}

