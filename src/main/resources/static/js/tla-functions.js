$(window).on('pageshow', init);

const copyStringToClipboard = (str) => {
    navigator.clipboard.writeText(str)
}

//const getCookie = (name) => Cookies.get(name)

/*const setSessionCookie = (name, value) => {
        let params = { 'samesite': 'Strict' }
        Cookies.set(name, value, params)
}*/

/*const setPermanentCookie = (name, value) => {
        let params = { 'expires': 365, 'samesite': 'Strict' }
        //console.log(`set ${name} to ${value}`)
        //console.log(JSON.stringify(params))
        Cookies.set(name, value, params)
}*/

const COOKIE_LIFETIME = 90;

const storeUserSetting = (name, value) => {
		if (cookiesAccepted()) {
			let params = { 'expires': COOKIE_LIFETIME, 'samesite': 'Strict' };  
			Cookies.set(name, value, params);
		}
		sessionStorage.setItem(name, value);
}

const getUserSetting = (name) => {
	var value = sessionStorage.getItem(name);
	
	if (!value) { // no info in sessionStorage
		value = Cookies.get(name);
		if (value) { // however, info in cookie
			sessionStorage.setItem(name, value);
			}
	}
	return value;
}

const setCookieAcceptance = (value) => {
	sessionStorage.setItem('Cookies_ok', value);
	
	if (value == 'true') {	
		let params = { 'samesite': 'Strict' }; // no 'expires' param => session cookie
		Cookies.set('Cookies_ok', value, params);
	}
}

const getCookiesAcceptance = () => {
	 var value = sessionStorage.getItem("Cookies_ok");
	 if (!value) { // no info in sessionStorage
		value = Cookies.get('Cookies_ok');
		if (value) { // however, info in cookie
			sessionStorage.setItem('Cookies_ok', value);
		}
	 }
	 return value;
}


const cookiesAccepted = () => {
	 return (getCookiesAcceptance() == 'true');
}

function init() {	
	// Abbreviation help links
	
	$('.ling-glossing').click(function(e) {
			e.preventDefault();
			window.open('/listings/ling-glossings','_blank');
		  });	

	$('.bts-glossing').click(function(e) {
			e.preventDefault();
			window.open('/listings/bts-glossings','_blank');
		  });	


//Cookies for Script choice
 $('#script1').click(function () {
		
			if(document.getElementById("script1").checked) {
				if( document.getElementById("script2").checked ){
					storeUserSetting("TranscriptionScript", "hieratic+demotic");
					//storeUserSetting("RootEncoding", "unicode");	
					storeUserSetting("TranscriptionEncoding", "unicode");	
					storeUserSetting("Mdc", "disabled");
					
					document.getElementById("transcription_enc_unicode").checked = true;
					//document.getElementById("root_enc_unicode").checked = true;
					//document.getElementById("root_enc_mdc").disabled = true;
					document.getElementById("transcription_enc_mdc").disabled = true;
               }
				
			}	
			else {
				 if(document.getElementById("script2").checked){
					storeUserSetting("TranscriptionScript", "demotic");	
					storeUserSetting("Mdc","enabled");
                
               //document.getElementById("root_enc_mdc").disabled = false;
                document.getElementById("transcription_enc_mdc").disabled = false;
                 }
               else {
	             document.getElementById("script2").checked=true;
					storeUserSetting("TranscriptionScript", "demotic");
					storeUserSetting("Mdc","enabled");

	             //document.getElementById("root_enc_mdc").disabled = false;
                document.getElementById("transcription_enc_mdc").disabled = false;
               }
			}
       });	

 $('#script2').click(function () {
		
			if(document.getElementById("script2").checked) {
				if( document.getElementById("script1").checked ){
					storeUserSetting("TranscriptionScript", "hieratic+demotic");
					//storeUserSetting("RootEncoding", "unicode");	
					storeUserSetting("TranscriptionEncoding", "unicode");	
					storeUserSetting("Mdc","disabled");

				document.getElementById("transcription_enc_unicode").checked = true;
		        //document.getElementById("root_enc_unicode").checked = true;
                //document.getElementById("root_enc_mdc").disabled = true;
                document.getElementById("transcription_enc_mdc").disabled = true;
               }
				
			}	
			else {
				 if(document.getElementById("script1").checked){
					storeUserSetting("TranscriptionScript", "hieratic");
					storeUserSetting("Mdc","enabled");
                
               //document.getElementById("root_enc_mdc").disabled = false;
                document.getElementById("transcription_enc_mdc").disabled = false;
                 }
               else {
	             document.getElementById("script1").checked=true;
					storeUserSetting("TranscriptionScript", "hieratic");
					storeUserSetting("Mdc","enabled");

	             //document.getElementById("root_enc_mdc").disabled = false;
                document.getElementById("transcription_enc_mdc").disabled = false;
               }
          
				
	}
       });	


	switch(getUserSetting("TranscriptionScript")) {
		case "hieratic+demotic":
			$('#script1').prop("checked",true);
			$('#script2').prop("checked",true); 
			break;
		case "demotic":
			$('#script1').prop("checked",false);
			$('#script2').prop("checked",true);
			break;
		case "hieratic":
		default:
			$('#script1').prop("checked",true);
			$('#script2').prop("checked",false); 
	}

	/*if ((getCookie("TranscriptionEncoding") == "unicode") ||(getCookie("TranscriptionEncoding") == null)){
			$('#transcription_enc_unicode').prop("checked", true);
			$('#root_enc_unicode').prop("checked", true);
			
				 document.getElementById("root_enc_mdc").disabled = true;
                document.getElementById("transcription_enc_mdc").disabled = true;
		}*/

	// Search form settings		  
//document.getElementById("transcription_enc_unicode").setAttribute("checked",true);
	// encoding radios
		if ((getUserSetting("TranscriptionEncoding") == "unicode") ){
			$('#transcription_enc_unicode').prop("checked", true);
			//$('#root_enc_unicode').prop("checked", true);
			if ((getUserSetting("Mdc") == "enabled") ){
				$('#transcription_enc_mdc').prop("disabled", false);
			    //$('#root_enc_mdc').prop("disabled", false);
			}
			else{
				$('#transcription_enc_mdc').prop("disabled", true);
			    //$('#root_enc_mdc').prop("disabled", true);
			}	
		}
		else {
			$('#transcription_enc_mdc').prop("checked", true);
			//$('#root_enc_mdc').prop("checked", true);
			}

        $('#transcription_enc_unicode').click(function () {
				storeUserSetting("TranscriptionEncoding", "unicode");
				//storeUserSetting("RootEncoding", "unicode");

				document.getElementById("transcription_enc_unicode").checked = true
				//document.getElementById("root_enc_unicode").checked = true
			
        });	
        $('#transcription_enc_mdc').click(function () {
				storeUserSetting("TranscriptionEncoding", "manuel_de_codage");
				//storeUserSetting("RootEncoding", "manuel_de_codage");
		
				document.getElementById("transcription_enc_mdc").checked = true;
				//document.getElementById("root_enc_mdc").checked = true;
			
        });	
        /* technically not possible yet
		  $('#root_enc_unicode').click(function () {
				storeUserSetting("RootEncoding", "unicode");
				storeUserSetting("TranscriptionEncoding", "unicode");

				document.getElementById("root_enc_unicode").checked = true;
				document.getElementById("transcription_enc_unicode").checked = true;
			
        });	
        $('#root_enc_mdc').click(function () {
				storeUserSetting("RootEncoding", "manuel_de_codage");
				storeUserSetting("TranscriptionEncoding", "manuel_de_codage");

				document.getElementById("root_enc_mdc").checked = true;
				document.getElementById("transcription_enc_mdc").checked = true;
			
        });*/	
//Translation Cookies

 $('#field-value-translation-checkbox-de-dict').click(function () {
				storeUserSetting("TranslationLang", "de");
				document.getElementById("field-value-translation-checkbox-de-dict").setAttribute("checked",true);				
			
        });	
 $('#field-value-translation-checkbox-en-dict').click(function () {
				storeUserSetting("TranslationLang", "en");
				document.getElementById("field-value-translation-checkbox-en-dict").setAttribute("checked",true);				
			
        });	

 $('#field-value-translation-checkbox-fr-dict').click(function () {
				storeUserSetting("TranslationLang", "fr");
				document.getElementById("field-value-translation-checkbox-fr-dict").setAttribute("checked",true);				
			
        });	

	switch(getUserSetting("TranslationLang")) {
		case "en":
			$('#field-value-translation-checkbox-en-dict').prop("checked", true);	
			break;
		case "fr":
			$('#field-value-translation-checkbox-fr-dict').prop("checked", true);
			break;
		case "de":
		default:
			$('#field-value-translation-checkbox-de-dict').prop("checked", true);
	}
		
	// Show/Hide - Buttons
    
		// .sidebar
		$('html').not('#sidebar').click(function (e) {
		 if ($('#sidebar').is(':visible') && !e.target == '#sidebar') {
                $('#sidebar').slideUp('ease-out');
            }
        });
        $('.sidebar-btn, .close-sidebar').click(function (e) {
			e.preventDefault();
            $('#sidebar').slideToggle('slow');
        });	
   
		
  	// .bts-glossing
		/*if (getCookie("TLAFlexcodeVisible") == "true") {
			//$('i', '.bts-glossing-btn').addClass("fa-plus-circle")
			$('.switchannotation-tlaglossing').show();
			}
		else {
			//$('i', '.bts-glossing-btn').addClass("fa-minus-circle")
			$('.switchannotation-tlaglossing').hide();
			}
*/
	/*	$('html').not('.switchannotation-tlaglossing').click(function (e) {
		 if ($('.switchannotation-tlaglossing').is(':visible') && !e.target == '.switchannotation-tlaglossing') {
                $('.switchannotation-tlaglossing').slideUp('ease-out');
            }
        });*/
      /*  $('#hide-property-button-switchannotation-tlaglossing').click(function (e) {
			e.preventDefault();
			 if ($('.switchannotation-tlaglossing').is(':visible') && !e.target == '.switchannotation-tlaglossing') {
                $('.switchannotation-tlaglossing').slideUp('ease-out');
            }
			if (sessionStorage.getItem("Cookies_ok") == "true") {
				if ($('.switchannotation-tlaglossing').is(':visible')) {
					setPrmanentCookie("TLAFlexcodeVisible", "false");
				} else {
					setPermanentCookie("TLAFlexcodeVisible", "true");
				}
			}
            $('.switchannotation-tlaglossing').slideToggle('slow');
        });
		*/
  	// .ling-glossing
	/*	if (getCookie("LingGlossingVisible") == "true") {
			$('i', '.ling-glossing-btn').addClass("fa-plus-circle")
			$('.ling-glossing').show();
			}
		else {
			$('i', '.ling-glossing-btn').addClass("fa-minus-circle")
			$('.ling-glossing').hide();
			}

		$('html').not('.ling-glossing').click(function (e) {
		 if ($('.ling-glossing').is(':visible') && !e.target == '.ling-glossing') {
                $('.ling-glossing').slideUp('ease-out');
            }
        });
        $('.ling-glossing-btn').click(function (e) {
			e.preventDefault();
			if (sessionStorage.getItem("Cookies_ok") == "true") {
				if ($('.ling-glossing').is(':visible')) {
					setPermanentCookie("LingGlossingVisible", "false");
				} else {
					setPermanentCookie("LingGlossingVisible", "true");
				}
			}
            $('.ling-glossing').slideToggle('slow');
        });	
		
		*/
		
		
  	// .token-translation
/*
	if (getCookie("TokenTranslationVisible") == "true") {
			$('i', '.token-translation-btn').addClass("fa-plus-circle")
			$('.token-translation').show();
			}
		else {
			$('i', '.token-translation-btn').addClass("fa-minus-circle")
			$('.token-translation').hide();
			}

		$('html').not('.token-translation').click(function (e) {
		 if ($('.token-translation').is(':visible') && !e.target == '.token-translation') {
                $('.token-translation').slideUp('ease-out');
            }
        });
        $('.token-translation-btn').click(function (e) {
			e.preventDefault();
			if (sessionStorage.getItem("Cookies_ok") == "true") {
				if ($('.token-translation').is(':visible')) {
					setPermanentCookie("TokenTranslationVisible", "false");
				} else {
					setPermanentCookie("TokenTranslationVisible", "true");
				}
			}
            $('.token-translation').slideToggle('slow');
        });	
		*/
  	// .text-date
	/*	if (getCookie("TextDateVisible") == "false") {
			$('i', '.text-date-btn').addClass("fa-minus-circle")
			$('.text-date').hide();
			}
		else {
			$('i', '.text-date-btn').addClass("fa-plus-circle")
			$('.text-date').show();
			}

		$('html').not('.text-date').click(function (e) {
		 if ($('.text-date').is(':visible') && !e.target == '.text-date') {
                $('.text-date').slideUp('ease-out');
            }
        });
        $('.text-date-btn').click(function (e) {
			e.preventDefault();
			if (sessionStorage.getItem("Cookies_ok") == "true") {
				if ($('.text-date').is(':visible')) {
					setPermanentCookie("TextDateVisible", "false");
				} else {
					setPermanentCookie("TextDateVisible", "true");
				}
			}
            $('.text-date').slideToggle('slow');
        });	
		*/
  	// .text-editor
	/*	if (getCookie("TextEditorVisible") == "false") {
			$('i', '.text-editor-btn').addClass("fa-minus-circle")
			$('.text-editor').hide();
			}
		else {
			$('i', '.text-editor-btn').addClass("fa-plus-circle")
			$('.text-editor').show();
			}

		$('html').not('.text-editor').click(function (e) {
		 if ($('.text-editor').is(':visible') && !e.target == '.text-editor') {
                $('.text-editor').slideUp('ease-out');
            }
        });
        $('.text-editor-btn').click(function (e) {
			e.preventDefault();
			if (sessionStorage.getItem("Cookies_ok") == "true") {
				if ($('.text-editor').is(':visible')) {
					setPermanentCookie("TextEditorVisible", "false");
				} else {
					setPermanentCookie("TextEditorVisible", "true");
				}
			}
            $('.text-editor').slideToggle('slow');
        });	
		
*/

	// .combination-search
		/*$('html').not('.combination-search').click(function (e) {
		 if ($('.combination-search').is(':visible') && !e.target == '.combination-search') {
                $('.combination-search').slideUp('ease-out');
            }
        });
        $('.combination-search-btn').click(function (e) {
			e.preventDefault();
            $('.combination-search').slideToggle('slow');
        });	*/
    
     // .corpus-path

       
    // .anno-block-btn
			if (getUserSetting("AnnotationBlockVisible") == "true") {
				//$('i', '.anno-block-btn').addClass("fa-minus-circle")
				$('.container-annotation-switch').show();
				$('.sentence-line-mode').hide();
			//	$('.container-annotation-switch-lines').hide();
				$('.indented-buttons-annotation').show();
				}
			else {
			//	$('i', '.anno-block-btn').addClass("fa-plus-circle")
				$('.container-annotation-switch').hide();
				$('.sentence-line-mode').show();
				//$('.container-annotation-switch-lines').show();
				$('.indented-buttons-annotation').hide();
				}


		$('html').not('.anno-block-btn').click(function (e) {
		 if ($('.container-annotation-switch').is(':visible') && !e.target == '.container-annotation-switch') {
                $('.container-annotation-switch').slideUp('ease-out');
            }
		 if ($('.indented-buttons-annotation').is(':visible') && !e.target == '.indented-buttons-annotation') {
                $('.indented-buttons-annotation').slideUp('ease-out');
            }
        });
        $('.anno-block-btn').click(function (e) {
			e.preventDefault();
			if ($('.container-annotation-switch').is(':visible')) {
				storeUserSetting("AnnotationBlockVisible", "false");
			} else {
				storeUserSetting("AnnotationBlockVisible", "true");
			}

            $('.container-annotation-switch').slideToggle('slow');
            //$('.container-annotation-switch-lines').slideToggle('slow');
            $('.indented-buttons-annotation').slideToggle('slow');
        });	
 

	// Show/Hide Comments
        
            $('.show-comment-button').click(function()  {    
             $('.comment-wrapper').toggleClass('hide-comment show-comment');             
            $('.show-comment-button').addClass('d-none');            
            $('.hide-comment-button').removeClass('d-none');
            
            });
        
    $('.hide-comment-button').click(function()  {
           $('.comment-wrapper').toggleClass('hide-comment show-comment');            
            $('.hide-comment-button').addClass('d-none');
            $('.show-comment-button').removeClass('d-none');
            });
        
		    
	// Headroom 
	    $(function() {
		var header = new Headroom(document.querySelector("#header"), {
			tolerance: 5,
			offset : 45,
			classes: {
			    // when element is initialised
				initial : "headroom",
				// when scrolling up
				pinned : "headroom--pinned",
				// when scrolling down
				unpinned : "headroom--unpinned",
				// when above offset
				top : "headroom--top"
			}
		});
		header.init();
		}());
		
		
	// change button-icons
	
    $(document).ready(function() {			
        $('.show-detail').click(function() {
	
    
        $("i", this).toggleClass("fas fa-plus-circle fas fa-minus-circle");
        
            });        	
			
		}); 
     
    // scrolltop
	
		$(document).ready(function(){
			$('.show-more').on('click', function(){
				$('html,body').animate({scrollTop: $(this).offset().top}, 800);
			}); 
		}); 
		
		
		//translation collapse
			if (getUserSetting("LanguagesButtonsVisible") == "true") {
			$('.indented-buttons-lang').show();
			}
		else {
			$('.indented-buttons-lang').hide();
			}

        $('html').not('.languages-btn').click(function (e) {
		 if ($('.indented-buttons-lang').is(':visible') && !e.target == '.indented-buttons-lang') {
                $('.indented-buttons-lang').slideUp('ease-out');
            }
        });
        $('.languages-btn').click(function (e) {
			e.preventDefault();
			
			if ($('.indented-buttons-lang').is(':visible')) {
				storeUserSetting("LanguagesButtonsVisible", "false");
			} else {
				storeUserSetting("LanguagesButtonsVisible", "true");
			}

            $('.indented-buttons-lang').slideToggle('slow');
        });

// Cookie Acceptance Banner ausblenden und Info in Footer anzeigen

	  if (!getCookiesAcceptance()) { // if "Cookies_ok" not set ("true" or "false")
		  $('.cookie-container').removeClass('d-none');
	  } 
	  if (getCookiesAcceptance() == 'true') {
		  $('#cookie-info').html('(Cookies o.k.)'); 
	  }
	
    $('.cookie-ok').click(function()  {
           $('.cookie-container').addClass('d-none');
			setCookieAcceptance('true');
			$('#cookie-info').html('(Cookies o.k.)'); 
            });
		
    $('.cookie-dismissed').click(function()  {
           $('.cookie-container').addClass('d-none');
			setCookieAcceptance('false');
             });
//$(document).ready(function() {
  //  $("#transliterationHelp").modal();
  //});
//$('#translationHelp').on('shown.bs.modal', function () {
  //$('#info').trigger('focus')
//})
//var options = {
//'backdrop' : 'static',
//'show':'true'
//}
//$('#translationHelp').modal('toggle');
}
function insertParam(key, value) {
        key = escape(key); value = escape(value);

        var kvp = document.location.search.substr(1).split('&');
        if (kvp == '') {
            document.location.search = '?' + key + '=' + value;
        }
        else {

            var i = kvp.length; var x; while (i--) {
                x = kvp[i].split('=');

                if (x[0] == key) {
                    x[1] = value;
                    kvp[i] = x.join('=');
                    break;
                }
            }

            if (i < 0) { kvp[kvp.length] = [key, value].join('='); }

            //this will reload the page, it's likely better to store this until finished
            document.location.search = kvp.join('&');
        }
    }


// returns current HREF including parameters
function getCurrentHREF() {
		return window.location.href;
}


// returns content of HTML element with id "full-citaion", if existent, 
// or else current HREF including parameters
function getCitation() {
		var cit =document.getElementById('full-citation');
		if (cit) {
			return cit.textContent.replaceAll(/\s+/g,' ');
		}
		else {
			return '<' + getCurrentHREF() + '>'; 
		}
}

