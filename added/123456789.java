package org.apache.solr.analysis;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
ffffffffffffffffffffffffffffffffffff
import java.io.Reader;
import java.io.StringReader;


 */
public class Test123 extends BaseTokenTestCase {
  /**
   * Test ArabicLetterTokenizerFactory
   * @deprecated (3.1) Remove in Lucene 5.0
   */
  @Deprecated
  public void testTokenizer() throws Exception {
    m = factory.create(reader);
    assertTokenStreamContents(stream, new String[] {"الذين", "مَلكت", "أيمانكم"});
 

Mel Badgett,  bill daniher, bill_daniher 


}