/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.remoting.client.communication

import com.canoo.remoting.client.communication.CommandAndHandler
import com.canoo.remoting.client.communication.CommandBatcher

class StandardCommandBatcherTest extends GroovyTestCase {

	CommandBatcher batcher

	@Override
	protected void setUp() throws Exception {
		batcher = new CommandBatcher()
	}

	void testEmpty() {
		assert batcher.isEmpty()
	}

	void testOne() {
		def cah = new CommandAndHandler()
		batcher.batch(cah)
        assert batcher.waitingBatches.val == [cah]
	}

	void testMultipleDoesNotBatch() {
		def list = [new CommandAndHandler()] * 3

		list.each { cwh -> batcher.batch(cwh) }

        assert batcher.waitingBatches.val == [list[0]]
        assert batcher.waitingBatches.val == [list[1]]
        assert batcher.waitingBatches.val == [list[2]]
	}
}
