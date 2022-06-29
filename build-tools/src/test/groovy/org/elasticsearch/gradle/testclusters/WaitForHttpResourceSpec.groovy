/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.gradle.testclusters

import spock.lang.Specification

import java.nio.file.Paths
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate

class WaitForHttpResourceSpec extends Specification {

    def "build trust store from trust store file"() {
        given:
        WaitForHttpResource http = new WaitForHttpResource(new URL("https://localhost/"))
        URL ca = getClass().getResource("/ca.p12")

        when:
        http.setTrustStoreFile(Paths.get(ca.toURI()).toFile())
        http.setTrustStorePassword("password")
        final KeyStore store = http.buildTrustStore()
        final Certificate certificate = store.getCertificate("ca")

        then:
        certificate != null
        certificate instanceof X509Certificate
        certificate.subjectX500Principal.toString() == 'CN=Elastic Certificate Tool Autogenerated CA'
    }

    def "build trust store from certificate authorities file"() {
        given:
        WaitForHttpResource http = new WaitForHttpResource(new URL("https://localhost/"))
        URL ca = getClass().getResource("/ca.pem")

        when:
        http.setCertificateAuthorities(Paths.get(ca.toURI()).toFile())
        KeyStore store = http.buildTrustStore()
        Certificate certificate = store.getCertificate("cert-0")

        then:
        certificate != null
        certificate instanceof X509Certificate
        certificate.subjectX500Principal.toString() == "CN=Elastic Certificate Tool Autogenerated CA"
    }
}