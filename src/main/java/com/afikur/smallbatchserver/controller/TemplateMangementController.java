package com.afikur.smallbatchserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;

@Controller
public class TemplateMangementController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getEditor() {
        return "edit";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String postEditor(@ModelAttribute("content") String content) {
        String cid = "demo";
        String docId = String.format("%06d", 1);
        String outputPath = "documents";

        try {
            String template = getTemplateString();
            generateHtml(cid, docId, template, content, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "edit";
    }

    private String getTemplateString() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("template/template.html"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String lineSeparator = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(lineSeparator);
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1); // delete the last new line separator
        reader.close();

        return stringBuilder.toString();
    }

    private void generateHtml(String cid, String docId, String template, String content, String outputPath) throws IOException {
        String[] multiplePagesConent = content.split("<p>(.+)?<!-- pagebreak -->(.+)?</p>");

        for (int i = 0; i < multiplePagesConent.length; i++) {
            String pageNumber = String.format("%04d", i + 1);
            String fileName = cid + "." + docId + "." + pageNumber + ".html";

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath + "/" + fileName));

            String htmlBody = template.replace("$body", multiplePagesConent[i].trim());
            writer.write(htmlBody);
            writer.close();
        }
    }
}
