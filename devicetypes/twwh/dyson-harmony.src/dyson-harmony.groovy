/**
 *  Dyson Harmony
 *
 *  Copyright 2017 Tony Wong
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
metadata {
	definition (name: "Dyson Harmony", namespace: "twwh", author: "Tony Wong") {
        capability "Thermostat"
        capability "Switch"
	}
    
	tiles(scale:2) {
    	standardTile("link", "device.thermostatMode", width: 6, height: 4, canChangeIcon: true) {
			state "off", label: "off", icon: "st.Appliances.appliances11", backgroundColor: "#ffffff"
			state "auto", label: "auto", icon: "st.Appliances.appliances11", backgroundColor: "#79b821"
            state "cool", label: "cool", icon: "st.Appliances.appliances11", backgroundColor: "#00ccff"
            state "heat", label: "heat", icon: "st.Appliances.appliances11", backgroundColor: "#ff6699"
            }
     	standardTile("linkoff", "device.thermostatMode", width: 3, height: 2, canChangeIcon: true, decoration: "flat") {
			state "off", label: "Off", action: "thermostatMode.off", icon: "st.Appliances.appliances11", backgroundColor: "#ffffff"
            }
		standardTile("linkauto", "device.thermostatMode", width: 3, height: 2, canChangeIcon: true, decoration: "flat") {
			state "auto", label: "Auto", action: "thermostatMode.auto", icon: "st.Weather.weather2", backgroundColor: "#ffffff"
            }
        standardTile("linkcool", "device.thermostatMode", width: 3, height: 2, canChangeIcon: true, decoration: "flat") {
			state "cool", label: "Cool", action: "thermostatMode.cool", icon: "st.Weather.weather1", backgroundColor: "#ffffff"
            }
        standardTile("linkheat", "device.thermostatMode", width: 3, height: 2, canChangeIcon: true, decoration: "flat") {
            state "heat", label: "Heat", action: "thermostatMode.heat", icon: "st.Weather.weather14", backgroundColor: "#ffffff"
            }   
        main("link")
	}
}

def off() {
    sendEvent(name: "thermostatMode", value: "off")
}

def auto() {
    sendEvent(name: "thermostatMode", value: "auto")
}

def cool() {
    sendEvent(name: "thermostatMode", value: "cool")
}

def heat() {
    sendEvent(name: "thermostatMode", value: "heat")
}
def setThermostatMode(value) {
	sendEvent(name: "thermostatMode", value: value)
}