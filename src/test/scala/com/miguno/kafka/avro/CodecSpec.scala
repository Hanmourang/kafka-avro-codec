package com.miguno.kafka.avro

import org.scalatest._
import com.miguno.avro.Tweet

class CodecSpec extends FlatSpec with Matchers {

  val BeginningOfEpoch = 0
  val AnyTimestamp = 1234
  val now = System.currentTimeMillis() / 1000

  val tweetEncoder = new AvroEncoder[Tweet](schema = Tweet.getClassSchema)
  val tweetDecoder = new AvroDecoder[Tweet](schema = Tweet.getClassSchema)

  def fixture = {
    new {
      val t1 = new Tweet("ANY_USER_1", "ANY_TEXT_1", now)
      val t2 = new Tweet("ANY_USER_2", "ANY_TEXT_2", BeginningOfEpoch)
      val t3 = new Tweet("ANY_USER_3", "ANY_TEXT_3", AnyTimestamp)

      val data = Seq(t1, t2, t3)
    }
  }

  "Kafka Avro codec" should "round trip a Tweet" in {
    val f = fixture
    f.data foreach {
      case t => {
        val actual = tweetDecoder.fromBytes(tweetEncoder.toBytes(t))
        actual.getUsername should be(t.getUsername)
        actual.getText should be(t.getText)
        actual.getTimestamp should be(t.getTimestamp)
      }
    }
  }

}