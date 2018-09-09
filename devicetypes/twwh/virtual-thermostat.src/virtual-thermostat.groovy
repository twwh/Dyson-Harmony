/**
 *  Copyright 2016 SmartThings, Inc.
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
	definition (
		name: "Virtual Thermostat",
		namespace: "twwh",
		author: "Tony") {

		capability "Thermostat"
		capability "Relative Humidity Measurement"
        capability "Switch"

		command "tempUp"
		command "tempDown"
		command "heatUp"
		command "heatDown"
		command "coolUp"
		command "coolDown"
		command "setTemperature", ["number"]
        command "setTemperatureReport", ["number"]
        command "setHumidity", ["number"]
        command "setHumiditySetpoint", ["number"]
        command "setCO2Alert", ["string"]

        attribute "humiditySetpoint", "number"
        attribute "humidifier", "enum", ["on", "off"]
        attribute "CO2Alert", "string"
	}
    
	tiles(scale: 2) {
		multiAttributeTile(name:"thermostatFull", type:"thermostat", width:6, height:4) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("temp", label:'${currentValue}°C', defaultState: true)
			}
			tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "tempUp")
				attributeState("VALUE_DOWN", action: "tempDown")
			}
			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
				attributeState("humidity", label:'${currentValue}%', unit:"%", defaultState: true)
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621")
				attributeState("heating", backgroundColor:"#e86d13")
				attributeState("cooling", backgroundColor:"#00A0DC")
                attributeState("dyson cooling", backgroundColor:"#00d8dc")
			}
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("off", label:'${name}')
				attributeState("heat", label:'${name}')
				attributeState("cool", label:'${name}')
				attributeState("auto", label:'${name}')
			}
			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState("heatingSetpoint", label:'${currentValue}°C', defaultState: true)
			}
			tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
				attributeState("coolingSetpoint", label:'${currentValue}°C', defaultState: true)
			}
		}
        
		valueTile("temperature", "device.temperature", width: 2, height: 2, canChangeIcon: true) {
			state("temp", label:'${currentValue}°C',
				backgroundColors:[
					[value: 10, color: "#153591"],
					[value: 15, color: "#1e9cbb"],
					[value: 20, color: "#90d2a7"],
					[value: 25, color: "#44b621"],
					[value: 30, color: "#f1d801"],
					[value: 35, color: "#d04e00"],
					[value: 40, color: "#bc2323"]
				]
			)
		}
        controlTile("heatingSetpointSlider", "device.heatingSetpoint", "slider", width: 2, height: 2, inactiveLabel: false, range:"(10..40)") {
            state "level", label:'Heat to ${currentValue}°C', unit: "°", action:"setHeatingSetpoint", backgroundColor: "#e86d13"
    	}	
        controlTile("coolingSetpointSlider", "device.coolingSetpoint", "slider", width: 2, height: 2, inactiveLabel: false, range:"(10..40)") {
            state "level", label:'Cool to ${currentValue}°C', unit: "°", action:"setCoolingSetpoint", backgroundColor: "#00A0DC"
    	}	
        controlTile("thermostatSetpointSlider", "device.thermostatSetpoint", "slider", width: 2, height: 2, inactiveLabel: false, range:"(10..40)") {
            state "level", label:'Set to ${currentValue}°C', unit: "°", action:"setTemperature", backgroundColor: "#44b621"
    	}
        controlTile("humiditySetpointSlider", "device.humiditySetpoint", "slider", width: 2, height: 2, inactiveLabel: false, range:"(40..70)") {
            state "level", label:'Humidify to ${currentValue}%', unit: "%", action:"setHumiditySetpoint", backgroundColor: "#153591"
    	}
        standardTile("mode", "device.thermostatMode", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "off", label:'Off', action:"thermostat.auto", backgroundColor:"#ffffff"
			state "heat", label:'Heat', action:"thermostat.off", backgroundColor:"#e86d13"
			state "cool", label:'Cool', action:"thermostat.off", backgroundColor:"#00A0DC"
			state "auto", label:'Auto', action:"thermostat.off", backgroundColor:"#00A0DC"
		}
		valueTile("CO2", "device.CO2Alert", width: 2, height: 2) {
    		state "safe", label: 'Safe', backgroundColor:"#ffffff"
            state "moderate", lablel: 'Moderate', backgroundColor:"#f1d801"
            state "harmful", label: 'Harmful', backgroundColor:"#bc2323"
        }

		main("temperature")
		details([
			"thermostatFull",
			"heatingSetpointSlider",
            "thermostatSetpointSlider",
			"coolingSetpointSlider",
            "humiditySetpointSlider",
            "CO2", "mode"
		])
	}
}

def installed() {
	sendEvent(name: "temperature", value: 22, unit: "C")
	sendEvent(name: "heatingSetpoint", value: 20, unit: "C")
	sendEvent(name: "thermostatSetpoint", value: 25, unit: "C")
	sendEvent(name: "coolingSetpoint", value: 28, unit: "C")
	sendEvent(name: "thermostatMode", value: "auto")
	sendEvent(name: "thermostatOperatingState", value: "idle")
	sendEvent(name: "humidity", value: 53, unit: "%")
    sendEvent(name: "humiditySetpoint", value: 55, unit: "%")
    sendEvent(name: "humidifier", value: "off")
    sendEvent(name: "humidifier", value: "safe")
}

def evaluate(temp, heatingSetpoint, coolingSetpoint) {
	def mode = device.currentValue("thermostatMode")
    
	if (mode in ["auto","heat","cool"]) {
		if (heatingSetpoint > temp) {
			sendEvent(name: "thermostatOperatingState", value: "heating")
            sendEvent(name: "thermostatMode", value: "heat")
		}
		else if ((heatingSetpoint < temp) && (temp < coolingSetpoint)) {
            sendEvent(name: "thermostatOperatingState", value: "idle")
            sendEvent(name: "thermostatMode", value: "auto")
		}
		else if ((temp > coolingSetpoint) && (temp < (coolingSetpoint - 2))) {
			sendEvent(name: "thermostatOperatingState", value: "dyson cooling")
		    sendEvent(name: "thermostatMode", value: "cool")
        }
		else if (temp > (coolingSetpoint - 2)) {
			sendEvent(name: "thermostatOperatingState", value: "cooling")
            sendEvent(name: "thermostatMode", value: "cool")
		}
    }        
}

def evaluate2 (thermostatSetpoint, heatingSetpoint, coolingSetpoint){
	if (thermostatSetpoint < heatingSetpoint){
    	sendEvent(name: "heatingSetpoint", value: thermostatSetpoint)
    }
    else if (thermostatSetpoint > coolingSetpoint) {
    	sendEvent(name: "coolingSetpoint", value: thermostatSetpoint)
    }
}

def evaluate3 (humidity, humiditysetpoint, humidifier){
	if (humidity < humiditysetpoint){
    	sendEvent(name: "humidifier", value: "on")
    }
    else {
    	sendEvent(name: "humidifier", value: "off")
    }
}

def setHeatingSetpoint(value) {
	sendEvent(name: "heatingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}

def setCoolingSetpoint(value) {
	sendEvent(name: "coolingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}

def setThermostatMode(String value) {
	sendEvent(name: "thermostatMode", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def off() {
	sendEvent(name: "thermostatMode", value: "off")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def heat() {
	sendEvent(name: "thermostatMode", value: "heat")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def auto() {
	sendEvent(name: "thermostatMode", value: "auto")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def cool() {
	sendEvent(name: "thermostatMode", value: "cool")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def tempUp() {
	def ts = device.currentState("thermostatSetpoint")
	def value = ts ? ts.integerValue + 1 : 40
	sendEvent(name:"thermostatSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
    evaluate2 (value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def tempDown() {
	def ts = device.currentState("thermostatSetpoint")
	def value = ts ? ts.integerValue - 1 : 40
	sendEvent(name:"thermostatSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
    evaluate2 (value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setTemperature(value) {
	sendEvent(name:"thermostatSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
	evaluate2 (value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def heatUp() {
	def ts = device.currentState("heatingSetpoint")
	def value = ts ? ts.integerValue + 1 : 40
	sendEvent(name:"heatingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}

def heatDown() {
	def ts = device.currentState("heatingSetpoint")
	def value = ts ? ts.integerValue - 1 : 40
	sendEvent(name:"heatingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}

def coolUp() {
	def ts = device.currentState("coolingSetpoint")
	def value = ts ? ts.integerValue + 1 : 40
	sendEvent(name:"coolingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}

def coolDown() {
	def ts = device.currentState("coolingSetpoint")
	def value = ts ? ts.integerValue - 1 : 40
	sendEvent(name:"coolingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}

def setTemperatureReport(value) {
	sendEvent(name:"temperature", value: value)
	evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setHumidity (value) {
	sendEvent(name:"humidity", value: value)
	evaluate3 (value, device.currentValue("humiditysetpoint"), device.currentValue("humidifier"))
}

def setHumiditySetpoint (value) {
 	sendEvent(name: "humiditySetpoint", value: value)
    evaluate3 (device.currentValue("humidity"), value, device.currentValue("humidifier"))
}

def on() {
	sendEvent(name: "thermostatMode", value: "auto")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setCO2Alert(String value){
		sendEvent(name: "CO2Alert", value: value)
}