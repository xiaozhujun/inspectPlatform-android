package org.whut.utils;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.whut.strings.FileStrings;



@SuppressWarnings("unchecked")
public class XmlUtils {

	public static List<String> getTableByUserRole(String userRole) throws Exception{
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(FileStrings.BASE_PATH+"/"+FileStrings.ROLE_TABLES));
		Element root = document.getRootElement();
		List<Element> elist = root.elements();
		for(Element e : elist){
			if(e.attribute("name").getValue().equals(userRole)){
				if(e.elements().size()>1){
					List<Element> list2 = e.elements();
					for(Element e2:list2){
						list.add(e2.attribute("name").getValue());
					}
				}else{
					Element ti = e.element("TableItem");
					list.add(ti.attribute("name").getValue());
				}
			}
		}
		return list;
	}

	public static List<String> getInspectLocation(String filePath) throws Exception{
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath));
		Element root = document.getRootElement();
		Element e = root.element("devicetype");
		List<Element> e1 = e.elements();
		for(Element e2 : e1){
			list.add(e2.attribute("name").getValue());
		}
		return list;
	}

	public static List<List<String>> getInspectField(String filePath) throws Exception{
		// TODO Auto-generated method stub

		List<List<String>> list = new ArrayList<List<String>>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath));
		Element root = document.getRootElement();
		Element e =root.element("devicetype");
		List<Element> e1 = e.elements();
		for(Element e2:e1){
			List<String> temp = new ArrayList<String>();
			List<Element> e3 = e2.elements();
			for(Element e4:e3){
				temp.add(e4.attribute("name").getValue());
			}
			list.add(temp);
		}	
		return list;
	}

	public static void saveInspectResult(List<List<Map<String,String>>> commentList,List<List<Integer>> result, String filePath) throws Exception{
		// TODO Auto-generated method stub
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath));
		Element root = document.getRootElement();
		Element e = root.element("devicetype");
		List<Element> list1 = e.elements();
		for(int i=0;i<list1.size();i++){
			List<Element> list2 = list1.get(i).elements();
			for(int j=0;j<list2.size();j++){
				switch(result.get(i).get(j)){
				case 0://正常
					list2.get(j).element("value").attribute("name").setValue("正常");
					break;
				case 1://异常
					list2.get(j).element("value").attribute("name").setValue("异常");
					break;
				case 2://无
					list2.get(j).element("value").attribute("name").setValue("无");
					break;
				}
				
				list2.get(j).element("value").attribute("comment").setValue(commentList.get(i).get(j).get("comment"));
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		String ENCODING = "UTF-8";
		format.setEncoding(ENCODING);
		format.setNewlines(true);
		XMLWriter writer=new XMLWriter(new java.io.FileOutputStream(filePath),format);
		writer.write(document);
		writer.close();	
	}


	public static void createFile(String filePath,int userId,String userName,String inspectTime) throws Exception{
		// TODO Auto-generated method stub
		//将固定格式的xml文件写入
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new File(filePath));
		Element root = document.getRootElement();
		root.attribute("workernumber").setValue(userId+"");
		root.attribute("worker").setValue(userName);
		root.attribute("inspecttime").setValue(inspectTime);
		Element e1 = root.element("devicetype");
		List<Element> e2 = e1.elements();
		Iterator<Element> it2 = e2.iterator();
		while (it2.hasNext()) {				
			Element e5 = it2.next();						
			List<Element> elements = e5.elements();
			Iterator<Element> it = elements.iterator();
			while (it.hasNext()) {
				Element e = it.next();					
				List<Element> group = e.elements();
				Iterator<Element> git = group.iterator();
				while (git.hasNext()) {
					Element ge = git.next();
					e.remove(ge);														
				}
				Element value=e.addElement("value");
				value.addAttribute("name", "正常");
				value.addAttribute("comment", "");
			}
		}

		OutputFormat format=OutputFormat.createPrettyPrint();
		String ENCODING="UTF-8";
		format.setEncoding(ENCODING);
		format.setNewlines(true);
		XMLWriter writer=new XMLWriter(new java.io.FileOutputStream(filePath),format);
		writer.write(document);
		writer.close();	

	}

	public static void updateInspectTable(String filePath, String deviceNum) throws Exception{
		// TODO Auto-generated method stub
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath));
		Element root = document.getRootElement();
		root.attribute("devicenumber").setValue(deviceNum);

		OutputFormat format=OutputFormat.createPrettyPrint();
		String ENCODING="UTF-8";
		format.setEncoding(ENCODING);
		format.setNewlines(true);
		XMLWriter writer=new XMLWriter(new java.io.FileOutputStream(filePath),format);
		writer.write(document);
		writer.close();	
	}

	public static List<List<Integer>> getInspectItemId(String filePath) throws Exception{
		// TODO Auto-generated method stub

		List<List<Integer>> list = new ArrayList<List<Integer>>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(filePath));
		Element root = document.getRootElement();
		Element e =root.element("devicetype");
		List<Element> e1 = e.elements();
		for(Element e2:e1){
			List<Integer> temp = new ArrayList<Integer>();
			List<Element> e3 = e2.elements();
			for(Element e4:e3){
				temp.add(Integer.parseInt(e4.attribute("itemId").getValue()));
			}
			list.add(temp);
		}	
		return list;
	}

}
