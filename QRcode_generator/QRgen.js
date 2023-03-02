/*
<html>
  <head>
    <script src="https://cdn.jsdelivr.net/npm/qrcode@0.18.0/build/qrcode.min.js"></script>
  </head>
  <body>
    <div id="qrcode"></div>
    <script>
      var qrcode = new QRCode("qrcode", {
        text: "Your raw data in HTML",
        width: 128,
        height: 128,
        colorDark : "#000000",
        colorLight : "#ffffff",
        correctLevel : QRCode.CorrectLevel.H
      });
    </script>
  </body>
</html>
*/
<script src="https://cdn.rawgit.com/davidshimjs/qrcodejs/gh-pages/qrcode.min.js"></script>
<div id="qrcode"></div>
  var qrcode = new QRCode(document.getElementById("qrcode"), {
    text: "Bonjour le monde !",
    width: 128,
    height: 128,
    colorDark : "#000000",
    colorLight : "#ffffff",
    correctLevel : QRCode.CorrectLevel.H    /*
  });

