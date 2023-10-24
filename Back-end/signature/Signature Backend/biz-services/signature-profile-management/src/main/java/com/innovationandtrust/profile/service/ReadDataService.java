package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.exception.ErrorXMLFormatException;
import com.innovationandtrust.utils.aping.signing.Actor;
import com.innovationandtrust.utils.aping.signing.SignProcessData;
import com.innovationandtrust.utils.aping.signing.SignStepData;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadDataService {
  public static List<SignProcessData> readXml(MultipartFile file) {
    try {
      InputStream xmlFile = file.getInputStream();
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // or prohibit the use of all protocols by external entities:
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(xmlFile);
      return getProcess(doc);
    } catch (Exception exception) {
      throw new ErrorXMLFormatException(exception.getMessage());
    }
  }

  private static List<SignProcessData> getProcess(Document doc) {
    NodeList processNodes = doc.getElementsByTagName("process");

    List<SignProcessData> signStepDataArrayList = new ArrayList<>();

    for (int i = 0; i < processNodes.getLength(); i++) {
      Node processNode = processNodes.item(i);
      if (processNode.getNodeType() == Node.ELEMENT_NODE) {
        Element processElement = (Element) processNode;
        int ttl =
            Integer.parseInt(processElement.getElementsByTagName("ttl").item(0).getTextContent());
        int templateId =
            Integer.parseInt(
                processElement.getElementsByTagName("templateId").item(0).getTextContent());
        NodeList documentNodes = processElement.getElementsByTagName("documents");
        ArrayList<String> documents = new ArrayList<>();
        for (int j = 0; j < documentNodes.getLength(); j++) {
          Node documentNode = documentNodes.item(j);
          if (documentNode.getNodeType() == Node.ELEMENT_NODE) {
            Element documentElement = (Element) documentNode;
            String document =
                documentElement.getElementsByTagName("document").item(0).getTextContent();
            documents.add(document);
          }
        }
        NodeList stepNodes = processElement.getElementsByTagName("step");
        List<SignStepData> signProcessDatas = getSignStepDatas(stepNodes);

        SignProcessData signProcessData =
            new SignProcessData(ttl, templateId, documents, signProcessDatas);
        signStepDataArrayList.add(signProcessData);
      }
    }

    return signStepDataArrayList;
  }

  private static List<SignStepData> getSignStepDatas(NodeList stepNodes){
    List<SignStepData> signProcessDatas = new ArrayList<>();
    for (int j = 0; j < stepNodes.getLength(); j++) {
      Node stepNode = stepNodes.item(j);
      if (stepNode.getNodeType() == Node.ELEMENT_NODE) {
        Element stepElement = (Element) stepNode;

        String tag = stepElement.getElementsByTagName("tag").item(0).getTextContent();
        int signatureType =
                Integer.parseInt(
                        stepElement.getElementsByTagName("signatureType").item(0).getTextContent());
        String cardinality =
                stepElement.getElementsByTagName("cardinality").item(0).getTextContent();

        List<Actor> actors = new ArrayList<>(0);

        NodeList actorNodes = stepElement.getElementsByTagName("actor");
        for (int k = 0; k < actorNodes.getLength(); k++) {
          Node actorNode = actorNodes.item(k);
          if (actorNode.getNodeType() == Node.ELEMENT_NODE) {
            Element actorElement = (Element) actorNode;

            String name = actorElement.getElementsByTagName("name").item(0).getTextContent();
            String firstName =
                    actorElement.getElementsByTagName("firstName").item(0).getTextContent();
            String userNanme =
                    actorElement.getElementsByTagName("userName").item(0).getTextContent();
            String email = actorElement.getElementsByTagName("email").item(0).getTextContent();
            String role = actorElement.getElementsByTagName("role").item(0).getTextContent();
            String mobile =
                    actorElement.getElementsByTagName("mobile").item(0).getTextContent();

            ArrayList<String> roles = new ArrayList<>();
            roles.add(role);

            Actor actor = new Actor(name, firstName, userNanme, email, roles, mobile);
            actors.add(actor);
          }
        }

        SignStepData signStepData = new SignStepData(tag, cardinality, signatureType, actors);
        signProcessDatas.add(signStepData);
      }
    }
    return signProcessDatas;
  }
}
