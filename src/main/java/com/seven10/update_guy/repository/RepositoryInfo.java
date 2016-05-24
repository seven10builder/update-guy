package com.seven10.update_guy.repository;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RepositoryInfo
{
    @XmlElement public String repoAddress;
    @XmlElement public String user;
    @XmlElement public String password;
    @XmlElement public String manifestPath;
}
