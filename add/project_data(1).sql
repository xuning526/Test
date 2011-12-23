-- MySQL dump 10.11
--
-- Host: localhost    Database: project_data
-- ------------------------------------------------------
-- Server version	5.0.67-0ubuntu6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `filesystem_repo`
--

DROP TABLE IF EXISTS `filesystem_repo`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `filesystem_repo` (
  `project_id` int(11) NOT NULL,
  `location` varchar(255) NOT NULL default '',
  `params` varchar(128) NOT NULL,
  `location_name` varchar(128) NOT NULL default '',
  `alias` varchar(128) NOT NULL default '',
  `last_updated` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  UNIQUE KEY `project_id` (`project_id`,`location`),
  CONSTRAINT `filesystem_repo_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project_table` (`project_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `filesystem_repo`
--

LOCK TABLES `filesystem_repo` WRITE;
/*!40000 ALTER TABLE `filesystem_repo` DISABLE KEYS */;
/*!40000 ALTER TABLE `filesystem_repo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_table`
--

DROP TABLE IF EXISTS `project_table`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_table` (
  `project_id` int(11) NOT NULL auto_increment,
  `host` varchar(64) NOT NULL default '',
  `short_name` varchar(64) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `files_uri` varchar(255) NOT NULL,
  `homepage` varchar(255) NOT NULL,
  `documentation_url` varchar(255) NOT NULL,
  `knowledgebase_url` varchar(255) NOT NULL,
  `bug_database_url` varchar(255) NOT NULL,
  `license` varchar(128) NOT NULL default '',
  `owner` varchar(128) NOT NULL default '',
  `boost` enum('IGNORE','LOW','NORMAL','HIGH') NOT NULL default 'NORMAL',
  `update_cycle` enum('HOURLY','DAILY','WEEKLY','MONTHLY','ONCE') NOT NULL default 'ONCE',
  `tuple_created` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `tuple_modified` timestamp NOT NULL default '0000-00-00 00:00:00',
  `feed_timestamp` timestamp NOT NULL default '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`project_id`),
  KEY `short_name` (`short_name`),
  KEY `name` (`name`),
  KEY `files_uri` (`files_uri`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `project_table`
--

LOCK TABLES `project_table` WRITE;
/*!40000 ALTER TABLE `project_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_table` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-03-15  4:39:02
