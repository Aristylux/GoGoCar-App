# Communication Blurtooth

## Table PID to Android

PID:

IDC: identification code

Module to App

|  PID  | Description                                     |  IDC  |    Data type    | message    |
| :---: | :---------------------------------------------- | :---: | :-------------: | :--------- |
|   5   | Engine coolant temperature                      | t_li  |       °C        | $t_li:data |
|  13   | Vehicle speed                                   | vit_  |      kl/h       | $vit_:data |
|  31   | Time since engine start                         | t_st  |        s        | $t_st:data |
|  33   | Distance traveled since a problem light came on | d_st  |       km        | $d_st:data |
|  47   | Fuel level                                      | l_cb  |        %        | $l_cb:data |
|  81   | Type of fuel used                               | carb  | Discrete values | $carb:data |
|  90   | Accelerator pedal position                      | pped  |        %        | $pped:data |
|  92   | Engine oil temperature                          | t_ol  |       °C        | $t_ol:data |
|  94   | Fuel consumption                                | cons  |       L/h       | $cons:data |
|  103  | Engine water temperature                        | t_wt  |       °C        | $t_wt:data |

App to Module

|  PID  | Description                  |  IDC  |    Data type     | message    |
| :---: | :--------------------------- | :---: | :--------------: | :--------- |
|       | Unblock (disable) the jammer | de_b  | 1 / 0 disable: 0 | $de_b:data |