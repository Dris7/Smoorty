## Smoorty - Client-Side Application

Smoorty is an Android app developed in Java that provides a simple user interface for predicting and solving handwritten equations. The app utilizes the server-side API provided by the serverSide repository to process and solve the equations. With Smoorty, users can capture images of handwritten equations or select existing images from their device to get accurate and instant solutions.
## Demo

### User Interface Screenshots
<img src="https://github.com/Dris7/Smoorty/assets/100499106/5746a078-4c52-43af-b41a-8d9d504f8aba" width="150">
<img src="https://github.com/Dris7/Smoorty/assets/100499106/6630c70d-432a-4372-9b92-3f1e2ce4c35a" width="150">
<img src="https://github.com/Dris7/Smoorty/assets/100499106/9583f4c8-5c0a-4d28-85ec-c001b254d50d" width="150">
<img src="https://github.com/Dris7/Smoorty/assets/100499106/2b1ee8f3-8013-4cfb-87e0-eb55d3feb2ef" width="150">
<img src="https://github.com/Dris7/Smoorty/assets/100499106/ef55807b-b48e-4264-973e-311692d4c974" width="150">

### Video Demo
[![Watch the video](/path/to/video_thumbnail.png)](/path/to/demo_video.mp4)

## Features
- Capture images of handwritten equations using the device camera.
- Select existing images from the device gallery.
- Send the images to the server-side API for equation processing.
- Receive the processed equation and solution as a JSON response.
- Display the equation and its solution on the user interface.
- User-friendly and intuitive UI for easy interaction.

## Prerequisites
- Android Studio (version X.X.X or higher)
- Java Development Kit (JDK X or higher)
- Android SDK
- Internet connectivity to communicate with the server-side API

## Installation
1. Clone or download the Smoorty repository to your local machine.
2. Open the project in Android Studio.
3. Build and run the app on your Android device or emulator.

## Usage
1. Launch the Smoorty app on your Android device.
2. Grant the necessary camera and storage permissions when prompted.
3. Choose one of the following options to input the handwritten equation:
   - Capture Image: Tap the camera icon to capture an image of the equation using the device camera.
   - Select Image: Tap the gallery icon to select an existing image from the device gallery.
4. The app will send the selected image to the server-side API for equation processing.
5. Wait for the response from the server-side API.
6. The app will display the processed equation and its solution on the user interface.

## Server-Side API Integration
Smoorty utilizes the server-side API provided by the serverSide repository to process and solve equations. Ensure that the server-side API is up and running with the correct URL endpoint. Update the API endpoint URL in the client-side code to establish a successful connection.

For detailed instructions on setting up and running the server-side API, please refer to the [serverSide repository](https://github.com/Dris7/Smart).

## Contributing
Contributions to Smoorty are welcome! If you encounter any issues or would like to add new features, please feel free to submit a pull request or raise an issue in the [Smoorty repository](https://github.com/username/smoorty).

## License
This project is licensed under the [MIT License](LICENSE).

## Acknowledgments
- [OpenCV](https://opencv.org) for providing computer vision libraries and algorithms.
- [Flask](https://flask.palletsprojects.com) for the server-side API framework.

For any questions or support, please contact the project contributors listed in the repository.
