/*
 * Copyright 2009-2011 WorldWide Conferencing, LLC
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

package net.liftweb
package json

import org.specs.Specification

object FieldSerializerExamples extends Specification {  
  import Serialization.{read, write => swrite}
  import FieldSerializer._

  val dog = new WildDog("black")
  dog.name = "pluto"
  dog.owner = Owner("joe", 35)

  "All fields are serialized by default" in {
    implicit val formats = DefaultFormats + FieldSerializer[WildDog]()
    val ser = swrite(dog)
    ser mustEqual """{"color":"black","name":"pluto","owner":{"name":"joe","age":35}}"""
    val dog2 = read[WildDog](ser) 
    dog2.name mustEqual dog.name
    dog2.color mustEqual dog.color
    dog2.owner mustEqual dog.owner
  }

  "Fields can be ignored and renamed" in {
    val dogSerializer = FieldSerializer[WildDog](
      rename("name", "animalname") andThen ignore("owner"),
      rename("animalname", "name")
    )

    implicit val formats = DefaultFormats + dogSerializer

    val ser = swrite(dog)
    ser mustEqual """{"color":"black","animalname":"pluto"}"""
    val dog2 = read[WildDog](ser) 
    dog2.name mustEqual dog.name
    dog2.color mustEqual dog.color
    dog2.owner mustEqual null
  }
}

abstract class Mammal {
  var name: String = ""
  var owner: Owner = null
}

class WildDog(val color: String) extends Mammal {
}

case class Owner(name: String, age: Int)
