const video = document.getElementById("video");
const canvas = document.getElementById("canvas");
const result = document.getElementById("result");


const codeReader = new ZXing.BrowserQRCodeReader();

function startCamera() {
  navigator.mediaDevices.getUserMedia({ video: true })
    .then(function (stream) {
      video.srcObject = stream;
      video.play();
      requestAnimationFrame(tick);
    });
}

function stopCamera() {
  video.pause();
  video.srcObject.getTracks()[0].stop();
}

function tick() {
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;
  canvas.getContext("2d").drawImage(video, 0, 0, canvas.width, canvas.height);
  const imageData = canvas.getContext("2d").getImageData(0, 0, canvas.width, canvas.height);
  try {
    const decoded = codeReader.decodeFromImageData(imageData);
    result.textContent = decoded.text;
    stopCamera();
  } catch (err) {
    requestAnimationFrame(tick);
  }
}

startCamera();
