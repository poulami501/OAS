package manageUpload;

import com.bea.control.ServiceControl;
import org.apache.beehive.controls.api.bean.ControlExtension;
import org.apache.beehive.controls.api.events.EventSet;
import weblogic.jws.wlw.UseWLW81BindingTypes;

/**
 * @jc:location http-url="http://localhost:7001/OrganizationManagementWebService/UploadDownloadManagementTest.jws"
 * @jc:wsdl file="#UploadDownloadManagementTestWsdl"
 * @editor-info:link autogen-style="java" source="UploadDownloadManagement.wsdl" autogen="true"
 */
@ControlExtension()
@ServiceControl.HttpSoapProtocol()
@ServiceControl.Location(urls = {
    "http://localhost:7001/OrganizationManagementWebService/UploadDownloadManagementTest.jws"
})
@ServiceControl.SOAPBinding(style = ServiceControl.SOAPBinding.Style.DOCUMENT, 
                            use = ServiceControl.SOAPBinding.Use.LITERAL, 
                            parameterStyle = ServiceControl.SOAPBinding.ParameterStyle.WRAPPED)
@ServiceControl.WSDL(path = "UploadDownloadManagement.wsdl",
                     service = "UploadDownloadManagementTest")
@UseWLW81BindingTypes()
public interface UploadService extends com.bea.control.ServiceControl
{
    
    @ServiceControl.SOAPBinding(style = ServiceControl.SOAPBinding.Style.DOCUMENT, 
                                use = ServiceControl.SOAPBinding.Use.LITERAL, 
                                parameterStyle = ServiceControl.SOAPBinding.ParameterStyle.WRAPPED)
    public void uploadFile(java.lang.String userName, java.lang.String serverFilePath, java.lang.Integer uploadDataFileId);

    static final long serialVersionUID = 1L;
	@EventSet(unicast = true)
	public interface Callback extends ServiceControl.Callback
	{
	}
}

/** @common:define name="UploadDownloadManagementTestWsdl" value::
    <?xml version="1.0" encoding="utf-8"?>
    <!-- @editor-info:link autogen="true" source="UploadDownloadManagementTest.jws" -->
    <definitions xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:conv="http://www.openuri.org/2002/04/soap/conversation/" xmlns:cw="http://www.openuri.org/2002/04/wsdl/conversation/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" xmlns:jms="http://www.openuri.org/2002/04/wsdl/jms/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:s0="http://www.openuri.org/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" targetNamespace="http://www.openuri.org/">
      <types>
        <s:schema elementFormDefault="qualified" targetNamespace="http://www.openuri.org/" xmlns:s="http://www.w3.org/2001/XMLSchema">
          <s:element name="uploadFile">
            <s:complexType>
              <s:sequence>
                <s:element name="userName" type="s:string" minOccurs="0"/>
                <s:element name="serverFilePath" type="s:string" minOccurs="0"/>
                <s:element name="uploadDataFileId" type="s:int" minOccurs="0"/>
              </s:sequence>
            </s:complexType>
          </s:element>
          <s:element name="uploadFileResponse">
            <s:complexType>
              <s:sequence/>
            </s:complexType>
          </s:element>
        </s:schema>
    
      </types>
      <message name="uploadFileSoapIn">
        <part name="parameters" element="s0:uploadFile"/>
      </message>
      <message name="uploadFileSoapOut">
        <part name="parameters" element="s0:uploadFileResponse"/>
      </message>
      <message name="uploadFileHttpGetIn">
        <part name="userName" type="s:string"/>
        <part name="serverFilePath" type="s:string"/>
        <part name="uploadDataFileId" type="s:string"/>
      </message>
      <message name="uploadFileHttpGetOut"/>
      <message name="uploadFileHttpPostIn">
        <part name="userName" type="s:string"/>
        <part name="serverFilePath" type="s:string"/>
        <part name="uploadDataFileId" type="s:string"/>
      </message>
      <message name="uploadFileHttpPostOut"/>
      <portType name="UploadDownloadManagementTestSoap">
        <operation name="uploadFile">
          <input message="s0:uploadFileSoapIn"/>
          <output message="s0:uploadFileSoapOut"/>
        </operation>
      </portType>
      <portType name="UploadDownloadManagementTestHttpGet">
        <operation name="uploadFile">
          <input message="s0:uploadFileHttpGetIn"/>
          <output message="s0:uploadFileHttpGetOut"/>
        </operation>
      </portType>
      <portType name="UploadDownloadManagementTestHttpPost">
        <operation name="uploadFile">
          <input message="s0:uploadFileHttpPostIn"/>
          <output message="s0:uploadFileHttpPostOut"/>
        </operation>
      </portType>
      <binding name="UploadDownloadManagementTestSoap" type="s0:UploadDownloadManagementTestSoap">
        <soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="document"/>
        <operation name="uploadFile">
          <soap:operation soapAction="http://www.openuri.org/uploadFile" style="document"/>
          <input>
            <soap:body use="literal"/>
          </input>
          <output>
            <soap:body use="literal"/>
          </output>
        </operation>
      </binding>
      <binding name="UploadDownloadManagementTestHttpGet" type="s0:UploadDownloadManagementTestHttpGet">
        <http:binding verb="GET"/>
        <operation name="uploadFile">
          <http:operation location="/uploadFile"/>
          <input>
            <http:urlEncoded/>
          </input>
          <output/>
        </operation>
      </binding>
      <binding name="UploadDownloadManagementTestHttpPost" type="s0:UploadDownloadManagementTestHttpPost">
        <http:binding verb="POST"/>
        <operation name="uploadFile">
          <http:operation location="/uploadFile"/>
          <input>
            <mime:content type="application/x-www-form-urlencoded"/>
          </input>
          <output/>
        </operation>
      </binding>
      <service name="UploadDownloadManagementTest">
        <port name="UploadDownloadManagementTestSoap" binding="s0:UploadDownloadManagementTestSoap">
          <soap:address location="http://localhost:7001/OrganizationManagementWebService/UploadDownloadManagementTest.jws"/>
        </port>
        <port name="UploadDownloadManagementTestHttpGet" binding="s0:UploadDownloadManagementTestHttpGet">
          <http:address location="http://localhost:7001/OrganizationManagementWebService/UploadDownloadManagementTest.jws"/>
        </port>
        <port name="UploadDownloadManagementTestHttpPost" binding="s0:UploadDownloadManagementTestHttpPost">
          <http:address location="http://localhost:7001/OrganizationManagementWebService/UploadDownloadManagementTest.jws"/>
        </port>
      </service>
    </definitions>
 * ::
 */
