CREATE TABLE  `zones` (
  `zoneID` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `dclass` varchar(6) NOT NULL,
  `primaryDNS` varchar(255) NOT NULL,
  `adminEmail` varchar(255) default NULL,
  `serial` int(10) unsigned default NULL,
  `refresh` int(10) unsigned default NULL,
  `retry` int(10) unsigned default NULL,
  `expire` int(10) unsigned default NULL,
  `minimum` int(10) unsigned default NULL,
  `secondary` tinyint(1) NOT NULL,
  `ttl` int(10) unsigned default NULL,
  `downloaded` timestamp NULL,
  PRIMARY KEY  (`zoneID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `records` (
  `recordID` int(10) unsigned NOT NULL auto_increment,
  `zoneID` int(10) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(6) NOT NULL,
  `content` varchar(255) NOT NULL,
  `ttl` int(10) unsigned default NULL,
  `dclass` varchar(6) NOT NULL,
  PRIMARY KEY  (`recordID`),
  KEY `FK_records_1` (`zoneID`),
  CONSTRAINT `FK_records_1` FOREIGN KEY (`zoneID`) REFERENCES `zones` (`zoneID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;