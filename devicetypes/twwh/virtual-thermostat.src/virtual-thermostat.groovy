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
        command "setCO2Alert", ["number"]
        command "CO2up"

        attribute "humiditySetpoint", "number"
        attribute "humidifier", "enum", ["on", "off"]
        attribute "CO2Alert", "number"
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
                attributeState("dyson", backgroundColor:"#00aaad")
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
        controlTile("humiditySetpointSlider", "device.humiditySetpoint", "slider", width: 2, height: 2, inactiveLabel: false, range:"(30..70)") {
            state "level", label:'Humidify to ${currentValue}%', unit: "%", action:"setHumiditySetpoint", backgroundColor: "#153591"
    	}
        valueTile("mode", "device.thermostatMode", width: 2, height: 2) {
			state "off", label:'Manual', action: "on", backgroundColor:"#778899"
            state "auto", label:'Auto', action: "off", backgroundColor:"#44b621"
			state "cool", label:'Cool', action: "off", backgroundColor:"#00A0DC"
            state "heat", label:'Heat', action: "off", backgroundColor:"#e86d13"
		}
		valueTile("CO2", "device.CO2Alert", width: 2, height: 2) {
    		state "1", label: 'Safe', backgroundColor:"#44b621"
            state "2", label: 'Moderate', backgroundColor:"#f1d801"
            state "3", label: 'Cautious', backgroundColor: "#f07800"
            state "4", label: 'Harmful', backgroundColor:"#bc2323"
        }
        valueTile("humidifiervalue", "device.humidifier", width: 2, height: 2) {
    		state "on", label: 'On', icon: "st.Weather.weather10", backgroundColor:"#ffffff"
            state "off", label: 'Off', icon: "st.Weather.weather10", backgroundColor:"#ffffff"
        }
        standardTile("modecool", "device.thermostatMode", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state "cool", label: "Cool", action: "cool", icon: "st.Weather.weather1", backgroundColor: "#ffffff"
        }
        standardTile("modeheat", "device.thermostatMode", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
            state "heat", label: "Heat", action: "heat", icon: "st.Weather.weather14", backgroundColor: "#ffffff"
        }        
        
		main("temperature")
		details([
			"thermostatFull",
			"heatingSetpointSlider", "thermostatSetpointSlider", "coolingSetpointSlider",
            "humiditySetpointSlider", "CO2", "mode" , 
            "humidifiervalue", "modeheat", "modecool"
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
	sendEvent(name: "humidity", value: 50, unit: "%")
    sendEvent(name: "humiditySetpoint", value: 50, unit: "%")
    sendEvent(name: "humidifier", value: "off")
    sendEvent(name: "CO2Alert", value: "1")
}

def evaluate(temp, heatingSetpoint, coolingSetpoint) {
	def mode = device.currentValue("thermostatMode")
	if (mode in ["auto"]) {
		if (heatingSetpoint >= temp) {
			sendEvent(name: "thermostatOperatingState", value: "heating")
		}
		else if ((heatingSetpoint < temp) && (temp < coolingSetpoint)) {
            sendEvent(name: "thermostatOperatingState", value: "idle")
		}
		else if ((temp < (coolingSetpoint + 2)) && (temp >= coolingSetpoint)) {
			sendEvent(name: "thermostatOperatingState", value: "dyson")
        }
		else if (temp >= coolingSetpoint + 2) {
			sendEvent(name: "thermostatOperatingState", value: "cooling")
		}
    }        
	else if (mode in ["heat"]) {
		if (heatingSetpoint >= temp) {
			sendEvent(name: "thermostatOperatingState", value: "heating")
		}
		else if (heatingSetpoint < temp) {
            sendEvent(name: "thermostatOperatingState", value: "idle")
		}
    }
    else if (mode in ["cool"]) {
    	if (temp < coolingSetpoint) {
            sendEvent(name: "thermostatOperatingState", value: "idle")
		}
   		else if ((temp < (coolingSetpoint + 2)) && (temp >= coolingSetpoint)) {
			sendEvent(name: "thermostatOperatingState", value: "dyson")
        }
		else if (temp >= coolingSetpoint + 2) {
			sendEvent(name: "thermostatOperatingState", value: "cooling")
		}
    }
}

def evaluate2(thermostatSetpoint, heatingSetpoint, coolingSetpoint){
	if (thermostatSetpoint < heatingSetpoint){
    	sendEvent(name: "heatingSetpoint", value: thermostatSetpoint)
    }
    else if (thermostatSetpoint > coolingSetpoint) {
    	sendEvent(name: "coolingSetpoint", value: thermostatSetpoint)
    }
}

def evaluate3(humidity, humiditySetpoint, humidifier){
	def mode = device.currentValue("thermostatMode")
    if (mode in ["auto","heat","cool"]) {
    	if (humidity <= humiditySetpoint){
    		sendEvent(name: "humidifier", value: "on")
    	}
   	 	else if (humidity > humiditySetpoint){
    		sendEvent(name: "humidifier", value: "off")
    	}
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

def setThermostatMode(value) {
	sendEvent(name: "thermostatMode", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
    evaluate3(device.currentValue("humidity"), device.currentValue("humiditySetpoint"), device.currentValue("humidifier"))
}

def off() {
	sendEvent(name: "thermostatMode", value: "off")
}

def heat() {
	sendEvent(name: "thermostatMode", value: "heat")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
    evaluate3(device.currentValue("humidity"), device.currentValue("humiditySetpoint"), device.currentValue("humidifier"))
}

def auto() {
	sendEvent(name: "thermostatMode", value: "auto")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def cool() {
	sendEvent(name: "thermostatMode", value: "cool")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
    evaluate3(device.currentValue("humidity"), device.currentValue("humiditySetpoint"), device.currentValue("humidifier"))
}

def tempUp() {
	def ts = device.currentState("thermostatSetpoint")
	def value = ts.integerValue + 1
	sendEvent(name:"thermostatSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
    evaluate2(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def tempDown() {
	def ts = device.currentState("thermostatSetpoint")
	def value = ts.integerValue - 1
	sendEvent(name:"thermostatSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
    evaluate2(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setTemperature(value) {
	sendEvent(name:"thermostatSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
	evaluate2(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def heatUp() {
	def ts = device.currentState("heatingSetpoint")
	def value = ts.integerValue + 1
	sendEvent(name:"heatingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}

def heatDown() {
	def ts = device.currentState("heatingSetpoint")
	def value = ts.integerValue - 1
	sendEvent(name:"heatingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}

def coolUp() {
	def ts = device.currentState("coolingSetpoint")
	def value = ts.integerValue + 1
	sendEvent(name:"coolingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}

def coolDown() {
	def ts = device.currentState("coolingSetpoint")
	def value = ts.integerValue - 1
	sendEvent(name:"coolingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}

def setTemperatureReport(value) {
	sendEvent(name:"temperature", value: value)
	evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setHumidity (value) {
	sendEvent(name:"humidity", value: value)
	evaluate3(value, device.currentValue("humiditySetpoint"), device.currentValue("humidifier"))
}

def setHumiditySetpoint (value) {
 	sendEvent(name: "humiditySetpoint", value: value)
    evaluate3(device.currentValue("humidity"), value, device.currentValue("humidifier"))
}

def on() {
	sendEvent(name: "thermostatMode", value: "auto")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
    evaluate3(device.currentValue("humidity"), device.currentValue("humiditySetpoint"), device.currentValue("humidifier"))
}

def setCO2Alert(value){
		sendEvent(name: "CO2Alert", value: value)
}

def CO2up(){
	def ts = device.currentState("CO2Alert")
	def value = ts.integerValue + 1
    def value2 = (value > 4) ? 4 : value
	sendEvent(name:"CO2Alert", value: value2)
}