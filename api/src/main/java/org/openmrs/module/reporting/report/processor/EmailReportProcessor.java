/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.report.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.Report;
import org.springframework.stereotype.Component;

/**
 * A ReportProcessor which sends the rendered report via email
 */
@Component
public class EmailReportProcessor implements ReportProcessor {
	
	protected Log log = LogFactory.getLog(this.getClass());
	private Session session = null;
	
	/**
	 * Returns the email session
	 */
	public Session getSession() {
		if (session == null) {

			AdministrationService as = Context.getAdministrationService();
			
			Properties p = new Properties();
			p.put("mail.transport.protocol", as.getGlobalProperty("mail.transport_protocol", "smtp"));
			p.put("mail.smtp.host", as.getGlobalProperty("mail.smtp_host", "localhost"));
			p.put("mail.smtp.port", as.getGlobalProperty("mail.smtp_port", "25")); // mail.smtp_port
			p.put("mail.smtp.auth", as.getGlobalProperty("mail.smtp_auth", "false")); // mail.smtp_auth
            p.put("mail.smtp.starttls.enable", as.getGlobalProperty("mail.smtp.starttls.enable", "false"));
			p.put("mail.debug", as.getGlobalProperty("mail.debug", "false"));
			p.put("mail.from", as.getGlobalProperty("mail.from", ""));
			
			final String user = as.getGlobalProperty("mail.user", "");
			final String password = as.getGlobalProperty("mail.password", "");
			
			if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)) {
				session = Session.getInstance(p, new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(user, password);
					}
				});
			}
			else {
				session = Session.getInstance(p);
			}
		}
		return session;
	}

	/**
	 * @see ReportProcessor#getConfigurationPropertyNames()
	 */
	public List<String> getConfigurationPropertyNames() {
		List<String> ret = new ArrayList<String>();
		ret.add("from");
		ret.add("to");
		ret.add("subject");
		ret.add("content");
		ret.add("addOutputToContent");
		ret.add("addOutputAsAttachment");
		ret.add("attachmentName");
		return ret;
	}

	/**
	 * Performs some action on the given report
	 * @param report the Report to process
	 */
	public void process(Report report, Properties configuration) {
		
		try {
			Message m = new MimeMessage(getSession());
			
			m.setFrom(new InternetAddress(configuration.getProperty("from")));
			for (String recipient : configuration.getProperty("to", "").split("\\,")) {
				m.addRecipient(RecipientType.TO, new InternetAddress(recipient));
			}
	
			// TODO: Make these such that they can contain report information
			m.setSubject(configuration.getProperty("subject"));
			
			Multipart multipart = new MimeMultipart();
			
			MimeBodyPart contentBodyPart = new MimeBodyPart();
			String content = configuration.getProperty("content", "");
			if (report.getRenderedOutput() != null && "true".equalsIgnoreCase(configuration.getProperty("addOutputToContent"))) {
				content += new String(report.getRenderedOutput());
			}
			contentBodyPart.setContent(content, "text/html");
			multipart.addBodyPart(contentBodyPart);
			
			if (report.getRenderedOutput() != null && "true".equalsIgnoreCase(configuration.getProperty("addOutputAsAttachment"))) {
				MimeBodyPart attachment = new MimeBodyPart();
				Object output = report.getRenderedOutput();
				if (report.getOutputContentType().contains("text")) {
					output = new String(report.getRenderedOutput(), "UTF-8");
				}
				attachment.setDataHandler(new DataHandler(output, report.getOutputContentType()));
				attachment.setFileName(configuration.getProperty("attachmentName"));
				multipart.addBodyPart(attachment);
			}
	
			m.setContent(multipart);
			
			Transport.send(m);
		}
		catch (Exception e) {
			throw new RuntimeException("Error occurred while sending report over email", e);
		}
	}
}
