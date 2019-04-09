# ISS-Tracker
International Space Station Online Tracker implemented using Kotlin, Coroutines for handling public open apis from International Space Station Current Location
official website http://open-notify.org/Open-Notify-API/ISS-Location-Now/
Here I used apis to get current whereabouts of space station http://api.open-notify.org/iss-now.json and currently how many peoples in space using
another public available api http://api.open-notify.org/astros.json

I used google map, to generate an api key for google map sdk for android go to this link  https://developers.google.com/maps/documentation/android-sdk/signup

![Screenshot2](https://user-images.githubusercontent.com/47312133/55790690-52ca4680-5ad6-11e9-82a0-bac590a386fd.png)
![screenshot1](https://user-images.githubusercontent.com/47312133/55790693-53fb7380-5ad6-11e9-8557-042f681231ab.png)


Used coroutines to show the current location of the ISS and update it every 5 seconds using timer:

    timer = Timer()
     timerTask = object : TimerTask() {
       
       override fun run() {
              
              handler.post(Runnable {
                  
                  CoroutineScope(Dispatchers.IO).launch {
                      
                      val request = apiService.getISSNow()
                       
                       try {
                         
                         val response = request.await()
                           
                           withContext(Dispatchers.Main) {
                            
                            if (response.message == "success") {
                        
                                initISSLocation(response.whereAbouts)
                         
                             }
                       
                         }
                      
                      } catch (e: HttpException) {
                            Log.d("REQUEST", "Exception ${e.message}")
                       
                       } catch (e: Throwable) {
                            Log.d("REQUEST", "Ooops: Something else went wrong")
                       
                      }

                    }
                })
            }
        }
        timer.schedule(timerTask, 5000, 5000)
