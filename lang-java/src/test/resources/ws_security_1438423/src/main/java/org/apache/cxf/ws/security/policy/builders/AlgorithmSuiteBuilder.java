/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.ws.security.policy.builders;



import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import org.apache.cxf.Bus;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.ws.security.policy.SP11Constants;
import org.apache.cxf.ws.security.policy.SP12Constants;
import org.apache.cxf.ws.security.policy.SPConstants;
import org.apache.cxf.ws.security.policy.WSSPolicyException;
import org.apache.cxf.ws.security.policy.custom.AlgorithmSuiteLoader;
import org.apache.cxf.ws.security.policy.custom.DefaultAlgorithmSuiteLoader;
import org.apache.cxf.ws.security.policy.model.AlgorithmSuite;
import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.builders.AssertionBuilder;


public class AlgorithmSuiteBuilder implements AssertionBuilder<Element> {
    
    private Bus bus;

    public AlgorithmSuiteBuilder(Bus bus) {
        this.bus = bus;
    }
    
    public Assertion build(Element element, AssertionBuilderFactory factory)
        throws IllegalArgumentException {
        
        SPConstants consts = SP11Constants.SP_NS.equals(element.getNamespaceURI())
            ? SP11Constants.INSTANCE : SP12Constants.INSTANCE;

        AlgorithmSuiteLoader loader = bus.getExtension(AlgorithmSuiteLoader.class);
        if (loader == null) {
            loader = new DefaultAlgorithmSuiteLoader();
        } 
        Element policyElement = DOMUtils.getFirstElement(element);
        if (policyElement == null) {
            throw new IllegalArgumentException(
                "sp:AlgorithmSuite/wsp:Policy must have a value"
            );
        }
        AlgorithmSuite algorithmSuite = null;
        try {
            algorithmSuite = loader.getAlgorithmSuite(policyElement, consts);
        } catch (WSSPolicyException e) {
            throw new IllegalArgumentException(e);
        }
        
        if (algorithmSuite == null && consts != SP11Constants.INSTANCE) {
            String algorithmSuiteName = DOMUtils.getFirstElement(policyElement).getLocalName();
            throw new IllegalArgumentException(
                "Algorithm suite \"" + algorithmSuiteName + "\" is not registered"
            );
        }

        return algorithmSuite;
    }

    public QName[] getKnownElements() {
        return new QName[]{SP11Constants.ALGORITHM_SUITE, SP12Constants.ALGORITHM_SUITE};
    }

}