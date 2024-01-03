$(document).ready(function () {
    $('#textInput, #colorInput1, #colorInput2, #applyCheckbox, #applyCheckbox2').on('input', function () {
      const inputText = $('#textInput').val();
      const color1 = $('#colorInput1').val();
      const color2 = $('#colorInput2').val();
      const applyGradient = $('#applyCheckbox').is(':checked');
      const applySymbol = $('#applyCheckbox2').is(':checked');
  
      const transformedText = transformText(inputText, color1, color2, applyGradient, applySymbol);
      $('#output').html(transformedText.replaceAll("##", "#"));
    });
  
    $('#output').on('click', function () {
      const outputText = $(this).text();
      copyToClipboard(outputText);
      animateCopyFeedback();
    });
  
    function transformText(text, color1, color2, applyGradient, applySymbol) {
      const letterMap = {
        'a': 'ᴀ', 'b': 'ʙ', 'c': 'ᴄ', 'd': 'ᴅ', 'e': 'ᴇ', 'f': 'ꜰ', 'g': 'ɢ',
        'h': 'ʜ', 'i': 'ɪ', 'j': 'ᴊ', 'k': 'ᴋ', 'l': 'ʟ', 'm': 'ᴍ', 'n': 'ɴ',
        'o': 'ᴏ', 'p': 'ᴘ', 'q': 'q', 'r': 'ʀ', 's': 'ꜱ', 't': 'ᴛ', 'u': 'ᴜ',
        'v': 'ᴠ', 'w': 'ᴡ', 'x': 'x', 'y': 'ʏ', 'z': 'ᴢ',
      };
  
      const transformedText = text.toLowerCase().split('').map(char => {
        return letterMap[char] || char;
      }).join('');
  
      const gradientText = getGradientText(transformedText, color1, color2, applyGradient, applySymbol);
  
      return gradientText.split(' ').map((word, index) => {
        if (word.startsWith('&#')) {
          return word; 
        }else if (word.startsWith('#')) {
          return word; 
        }
  
        return `${calculateGradientColor(color1, color2, index / (transformedText.length - 1))}${word}`;
      }).join(' ');
    }
  
    function calculateGradientColor(color1, color2, ratio) {
      const rgb1 = hexToRgb(color1);
      const rgb2 = hexToRgb(color2);
  
      const r = Math.round(rgb1.r + (rgb2.r - rgb1.r) * ratio);
      const g = Math.round(rgb1.g + (rgb2.g - rgb1.g) * ratio);
      const b = Math.round(rgb1.b + (rgb2.b - rgb1.b) * ratio);
  
      return rgbToHex(r, g, b);
    }
  
    function getGradientText(text, color1, color2, applyGradient, applySymbol) {
      const gradientSteps = text.length;
      let gradientText = "";
  
      for (let i = 0; i < gradientSteps; i++) {
        const gradientColor = calculateGradientColor(color1, color2, i / (gradientSteps - 1));
        if(applySymbol){
          gradientText += `&`;
        }
        if(applyGradient){
            gradientText += `#${gradientColor}&l${text[i]}`;
        }else{
            gradientText += `#${gradientColor}${text[i]}`;
        }
        
      }
  
      return gradientText;
    }
  
    function hexToRgb(hex) {
      const bigint = parseInt(hex.substring(1), 16);
      const r = (bigint >> 16) & 255;
      const g = (bigint >> 8) & 255;
      const b = bigint & 255;
      return { r, g, b };
    }
  
    function rgbToHex(r, g, b) {
      return "#"+(1 << 24 | r << 16 | g << 8 | b).toString(16).slice(1);
    }
  
    function copyToClipboard(text) {
      const el = document.createElement('textarea');
      el.value = text;
      document.body.appendChild(el);
      el.select();
      document.execCommand('copy');
      document.body.removeChild(el);
    }
  
    function animateCopyFeedback() {
      $('#output').animate({
        backgroundColor: '#2ecc71',
        color: '#ffffff'
      }, 300, function () {
        $(this).animate({
          backgroundColor: '#ecf0f1',
          color: '#2c3e50'
        }, 300);
      });
    }
  });
  