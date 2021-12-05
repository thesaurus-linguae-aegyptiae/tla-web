$(window).on('pageshow', init);

const copyStringToClipboard = (str) => {
    navigator.clipboard.writeText(str)
}

const getCookie = (name) => Cookies.get(name)

//const setCookie = (name, value, path = undefined) => {

const setCookie = (name, value) => {
   // if (Cookies.get("accepted")) {
        let params = { expires: 365 }
      /*  if (path !== undefined) {
            params = {
                path: path,
                ...params
            }
        }*/
        console.log(`set ${name} to ${value}`)
        console.log(JSON.stringify(params))
        Cookies.set(name, value, params)
   // }
}


function init() {	

    // Cookie Acceptance Banner ausblenden

	   if (Cookies.get("accepted")) {
       $('#cookie-info').html('(Cookies accepted)');}
    

	// Search-Button


	// Abbreviation help links
	
	$('.ling-glossing').click(function(e) {
			e.preventDefault();
			window.open('/help/ling-glossings','_blank');
		  });	

	$('.bts-glossing').click(function(e) {
			e.preventDefault();
			window.open('/help/bts-glossings','_blank');
		  });	


//Cookies for Script choice
 $('#script1').click(function () {
		
			if(document.getElementById("script1").checked) {
				if( document.getElementById("script2").checked ){
				if (getCookie("CookiePolicy") == "accepted") {
				          setCookie("TranscriptionScript", "hieratic+demotic");
		
					    setCookie("RootEncoding", "unicode");	
						setCookie("TranscriptionEncoding", "unicode");	
						setCookie("Mdc", "disabled");
                   }
                 else{
	              sessionStorage.setItem("TranscriptionScript", "hieratic+demotic");
		
					   sessionStorage.setItem("RootEncoding", "unicode");	
						sessionStorage.setItem("TranscriptionEncoding", "unicode");
						sessionStorage.setItem("Mdc","disabled");
                }
				document.getElementById("transcription_enc_unicode").checked = true;
		        document.getElementById("root_enc_unicode").checked = true;
                document.getElementById("root_enc_mdc").disabled = true;
                document.getElementById("transcription_enc_mdc").disabled = true;
               }
				
			}	
			else {
				 if(document.getElementById("script2").checked){
			             if (getCookie("CookiePolicy") == "accepted") 
                         {
			                   setCookie("TranscriptionScript", "demotic");	
                               setCookie("Mdc","enabled");
                          }
                         else {sessionStorage.setItem("TranscriptionScript", "demotic");
                                   sessionStorage.setItem("Mdc","enabled");
                            }
                
               document.getElementById("root_enc_mdc").disabled = false;
                document.getElementById("transcription_enc_mdc").disabled = false;
                 }
               else {
	             document.getElementById("script2").checked=true;
                     if (getCookie("CookiePolicy") == "accepted") 
                     {
			                   setCookie("TranscriptionScript", "demotic");
                             setCookie("Mdc","enabled");
                       }
                     else {
	                     sessionStorage.setItem("TranscriptionScript", "demotic");
                         sessionStorage("Mdc","enabled");
                      }
	             document.getElementById("root_enc_mdc").disabled = false;
                document.getElementById("transcription_enc_mdc").disabled = false;
               }
          
				
	}
       });	

 $('#script2').click(function () {
		
			if(document.getElementById("script2").checked) {
				if( document.getElementById("script1").checked ){
				if (getCookie("CookiePolicy") == "accepted") {
				          setCookie("TranscriptionScript", "hieratic+demotic");
                           setCookie("RootEncoding", "unicode");	
						setCookie("TranscriptionEncoding", "unicode");	
						setCookie("Mdc","disabled");
                   }
                else{
	               sessionStorage.setItem("TranscriptionScript", "hieratic+demotic");
                   sessionStorage.setItem("RootEncoding", "unicode");	
				sessionStorage.setItem("TranscriptionEncoding", "unicode");	
	             sessionStorage.setItem("Mdc","enabled");
                }
				document.getElementById("transcription_enc_unicode").checked = true;
		        document.getElementById("root_enc_unicode").checked = true;
                document.getElementById("root_enc_mdc").disabled = true;
                document.getElementById("transcription_enc_mdc").disabled = true;
               }
				
			}	
			else {
				 if(document.getElementById("script1").checked){
			             if (getCookie("CookiePolicy") == "accepted") {
                     
			                   setCookie("TranscriptionScript", "hieratic");
                               setCookie("Mdc","enabled");
                         }
                         else {
	                          sessionStorage.setItem("TranscriptionScript", "hieratic");
                              sessionStorage("Mdc","enabled");	
                       }
                
               document.getElementById("root_enc_mdc").disabled = false;
                document.getElementById("transcription_enc_mdc").disabled = false;
                 }
               else {
	             document.getElementById("script1").checked=true;
                     if (getCookie("CookiePolicy") == "accepted") {
                     
			                   setCookie("TranscriptionScript", "hieratic");
                               setCookie("Mdc","enabled");
                     }
                     else {
	                    sessionStorage.setItem("TranscriptionScript", "hieratic");
                       sessionStorage.setItem("Mdc","enabled");
                     }
	             document.getElementById("root_enc_mdc").disabled = false;
                document.getElementById("transcription_enc_mdc").disabled = false;
               }
          
				
	}
       });	
 
if (getCookie("CookiePolicy") == "accepted") {
 if ((getCookie("TranscriptionScript") == "demotic")){
 $('#script2').prop("checked",true);
}
 if ((getCookie("TranscriptionScript") == "hieratic")){
 $('#script1').prop("checked",true);
}
 if ((getCookie("TranscriptionScript") == "hieratic+demotic")){
 $('#script2').prop("checked",true); $('#script1').prop("checked",true);
}

}
else{
	 if ((sessionStorage.getItem("TranscriptionScript") == "demotic")){
 $('#script2').prop("checked",true);
}
 if ((sessionStorage.getItem("TranscriptionScript") == "hieratic")){
 $('#script1').prop("checked",true);
}
 if ((sessionStorage.getItem("TranscriptionScript") == "hieratic+demotic")){
 $('#script2').prop("checked",true); $('#script1').prop("checked",true);
}
	
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
	if(getCookie("CookiePolicy") == "accepted"){
		if ((getCookie("TranscriptionEncoding") == "unicode") ){
			$('#transcription_enc_unicode').prop("checked", true);
			$('#root_enc_unicode').prop("checked", true);
			if ((getCookie("Mdc") == "enabled") ){
				$('#transcription_enc_mdc').prop("disabled", false);
			    $('#root_enc_mdc').prop("disabled", false);
			}
			else{
				$('#transcription_enc_mdc').prop("disabled", true);
			    $('#root_enc_mdc').prop("disables", true);
			}	
		}
		else {
			$('#transcription_enc_mdc').prop("checked", true);
			$('#root_enc_mdc').prop("checked", true);
			}
	}
	else{
		if ((sessionStorage.getItem("TranscriptionEncoding") == "unicode") ){
			$('#transcription_enc_unicode').prop("checked", true);
			$('#root_enc_unicode').prop("checked", true);
			if ((sessionStorage.getItem("Mdc") == "enabled") ){
				$('#transcription_enc_mdc').prop("disabled", false);
			    $('#root_enc_mdc').prop("disabled", false);
			}
			else{
				$('#transcription_enc_unicode').prop("disabled", true);
			    $('#root_enc_unicode').prop("disabled", true);
			}	
		}
		else {
			$('#transcription_enc_mdc').prop("checked", true);
			$('#root_enc_mdc').prop("checked", true);
			}
	}		

        $('#transcription_enc_unicode').click(function () {
			if (getCookie("CookiePolicy") == "accepted") {
				setCookie("TranscriptionEncoding", "unicode");
				setCookie("RootEncoding", "unicode");
				}
			else{
				sessionStorage.setItem("TranscriptionEncoding", "unicode");
				sessionStorage.setItem("RootEncoding", "unicode");
			}	
				document.getElementById("transcription_enc_unicode").checked = true
				document.getElementById("root_enc_unicode").checked = true
			
        });	
        $('#transcription_enc_mdc').click(function () {
			if (getCookie("CookiePolicy") == "accepted") {
				setCookie("TranscriptionEncoding", "manuel_de_codage");
				setCookie("RootEncoding", "manuel_de_codage");
				}
			else{
				sessionStorage.setItem("TranscriptionEncoding", "manuel_de_codage");
				sessionStorage.setItem("RootEncoding", "manuel_de_codage");
				
			}
		
				document.getElementById("transcription_enc_mdc").checked = true;
				document.getElementById("root_enc_mdc").checked = true;
			
        });	
        $('#root_enc_unicode').click(function () {
			if (getCookie("CookiePolicy") == "accepted") {
				setCookie("RootEncoding", "unicode");
				setCookie("TranscriptionEncoding", "unicode");
				}
				else{
					sessionStorage.setItem("RootEncoding", "unicode");
				sessionStorage.setItem("TranscriptionEncoding", "unicode");
				}
				document.getElementById("root_enc_unicode").checked = true;
				document.getElementById("transcription_enc_unicode").checked = true;
			
        });	
        $('#root_enc_mdc').click(function () {
			if (getCookie("CookiePolicy") == "accepted") {
				setCookie("RootEncoding", "manuel_de_codage");
				setCookie("TranscriptionEncoding", "manuel_de_codage");
				}
			else{
				sessionStorage.setItem("RootEncoding", "manuel_de_codage");
				sessionStorage.setItem("TranscriptionEncoding", "manuel_de_codage");
			}	
				document.getElementById("root_enc_mdc").checked = true;
				document.getElementById("transcription_enc_mdc").checked = true;
			
        });	
//Translation Cookies

 $('#field-value-translation-checkbox-de-dict').click(function () {
			if (getCookie("CookiePolicy") == "accepted") {
				setCookie("TranslationLang", "de");
				}
			else{
				sessionStorage.setItem("TranslationLang", "de");
			}	
				document.getElementById("field-value-translation-checkbox-de-dict").setAttribute("checked",true);				
			
        });	
 $('#field-value-translation-checkbox-en-dict').click(function () {
			if (getCookie("CookiePolicy") == "accepted") {
				setCookie("TranslationLang", "en");
				}
			else{
				sessionStorage.setItem("TranslationLang", "en");
			}	
				document.getElementById("field-value-translation-checkbox-en-dict").setAttribute("checked",true);				
			
        });	

 $('#field-value-translation-checkbox-fr-dict').click(function () {
			if (getCookie("CookiePolicy") == "accepted") {
				setCookie("TranslationLang", "fr");
				}
			else{
				sessionStorage.setItem("TranslationLang", "fr");
			}	
				document.getElementById("field-value-translation-checkbox-fr-dict").setAttribute("checked",true);				
			
        });	

if(getCookie("CookiePolicy") == "accepted"){
		if ((getCookie("TranslationLang") == "de") )
			$('#field-value-translation-checkbox-de-dict').prop("checked", true);
		else if ((getCookie("TranslationLang") == "en") )
			$('#field-value-translation-checkbox-en-dict').prop("checked", true);	
		else if ((getCookie("TranslationLang") == "fr") )
			$('#field-value-translation-checkbox-fr-dict').prop("checked", true);
}
else	{
	
	if ((sessionStorage.getItem("TranslationLang") == "de") )
			$('#ield-value-translation-checkbox-de-dict').prop("checked", true);
		else if ((getCookie("TranslationLang") == "en") )
			$('#field-value-translation-checkbox-en-dict').prop("checked", true);	
		else if ((getCookie("TranslationLang") == "fr") )
			$('#field-value-translation-checkbox-fr-dict').prop("checked", true);
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
		if (getCookie("TLAFlexcodeVisible") == "true") {
			$('i', '.bts-glossing-btn').addClass("fa-plus-circle")
			$('.bts-glossing').show();
			}
		else {
			$('i', '.bts-glossing-btn').addClass("fa-minus-circle")
			$('.bts-glossing').hide();
			}

		$('html').not('.bts-glossing').click(function (e) {
		 if ($('.bts-glossing').is(':visible') && !e.target == '.bts-glossing') {
                $('.bts-glossing').slideUp('ease-out');
            }
        });
        $('.bts-glossing-btn').click(function (e) {
			e.preventDefault();
			if (getCookie("CookiePolicy") == "accepted") {
				if ($('.bts-glossing').is(':visible')) {
					setCookie("TLAFlexcodeVisible", "false",{expires:365});
				} else {
					setCookie("TLAFlexcodeVisible", "true",{expires:365});
				}
			}
            $('.bts-glossing').slideToggle('slow');
        });	
		
  	// .ling-glossing
		if (getCookie("LingGlossingVisible") == "true") {
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
			if (getCookie("CookiePolicy") == "accepted") {
				if ($('.ling-glossing').is(':visible')) {
					setCookie("LingGlossingVisible", "false",{expires:365});
				} else {
					setCookie("LingGlossingVisible", "true",{expires:365});
				}
			}
            $('.ling-glossing').slideToggle('slow');
        });	
		
		
		
		
  	// .token-translation

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
			if (getCookie("CookiePolicy") == "accepted") {
				if ($('.token-translation').is(':visible')) {
					setCookie("TokenTranslationVisible", "false",{expires:365});
				} else {
					setCookie("TokenTranslationVisible", "true",{expires:365});
				}
			}
            $('.token-translation').slideToggle('slow');
        });	
		
  	// .text-date
		if (getCookie("TextDateVisible") == "false") {
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
			if (getCookie("CookiePolicy") == "accepted") {
				if ($('.text-date').is(':visible')) {
					setCookie("TextDateVisible", "false",{expires:365});
				} else {
					setCookie("TextDateVisible", "true",{expires:365});
				}
			}
            $('.text-date').slideToggle('slow');
        });	
		
  	// .text-editor
		if (getCookie("TextEditorVisible") == "false") {
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
			if (getCookie("CookiePolicy") == "accepted") {
				if ($('.text-editor').is(':visible')) {
					setCookie("TextEditorVisible", "false",{expires:365});
				} else {
					setCookie("TextEditorVisible", "true",{expires:365});
				}
			}
            $('.text-editor').slideToggle('slow');
        });	
		


	// .combination-search
		$('html').not('.combination-search').click(function (e) {
		 if ($('.combination-search').is(':visible') && !e.target == '.combination-search') {
                $('.combination-search').slideUp('ease-out');
            }
        });
        $('.combination-search-btn').click(function (e) {
			e.preventDefault();
            $('.combination-search').slideToggle('slow');
        });	
    
     // .corpus-path

        
    // .anno-block-btn
		if (getCookie("AnnotationBlockVisible") == "true") {
			$('i', '.anno-block-btn').addClass("fa-plus-circle")
			$('.container-annotation-switch-anno').show();
			$('.container-annotation-switch-lines').hide();
			$('.indented-buttons-annotation').show();
			}
		else {
			$('i', '.anno-block-btn').addClass("fa-minus-circle")
			$('.container-annotation-switch-anno').hide();
			$('.container-annotation-switch-lines').show();
			$('.indented-buttons-annotation').hide();
			}

		$('html').not('.anno-block-btn').click(function (e) {
		 if ($('.container-annotation-switch-anno').is(':visible') && !e.target == '.container-annotation-switch-anno') {
                $('.container-annotation-switch-anno').slideUp('ease-out');
            }
		 if ($('.indented-buttons-annotation').is(':visible') && !e.target == '.indented-buttons-annotation') {
                $('.indented-buttons-annotation').slideUp('ease-out');
            }
        });
        $('.anno-block-btn').click(function (e) {
			e.preventDefault();
			if (getCookie("CookiePolicy") == "accepted") {
				if ($('.container-annotation-switch-anno').is(':visible')) {
					setCookie("AnnotationBlockVisible", "false",{expires:365});
				} else {
					setCookie("AnnotationBlockVisible", "true",{expires:365});
				}
			}
            $('.container-annotation-switch-anno').slideToggle('slow');
            $('.container-annotation-switch-lines').slideToggle('slow');
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
		if (Cookies.get("CookiePolicy") == "accepted") {
			if (Cookies.get("LanguagesButtonsVisible") == "true") {
			$('.indented-buttons-lang').show();
			}
		else {
			$('.indented-buttons-lang').hide();
			}
			}
		else{ if (sessionStorage.getItem("LanguagesButtonsVisible") == "true") {
			$('.indented-buttons-lang').show();
			}
		else {
			$('.indented-buttons-lang').hide();
			}
			
		}	

        $('html').not('.languages-btn').click(function (e) {
		 if ($('.indented-buttons-lang').is(':visible') && !e.target == '.indented-buttons-lang') {
                $('.indented-buttons-lang').slideUp('ease-out');
            }
        });
        $('.languages-btn').click(function (e) {
			e.preventDefault();
			
			if (Cookies.get("CookiePolicy") == "accepted") {
				if ($('.indented-buttons-lang').is(':visible')) {
					Cookies.set("LanguagesButtonsVisible", "false",{expires:365});
				} else {
					Cookies.set("LanguagesButtonsVisible", "true",{expires:365});
				}
			}
			else{
				if ($('.indented-buttons-lang').is(':visible')) {
					sessionStorage.setItem("LanguagesButtonsVisible", "false");
				} else {
					sessionStorage.setItem("LanguagesButtonsVisible", "true");
				}
				
			}
            $('.indented-buttons-lang').slideToggle('slow');
        });


    // Cookie Acceptance Banner ausblenden

	  var cookieAcceptanceState = getCookie("CookiePolicy");
	  if (cookieAcceptanceState == "accepted") {
		  $('.cookie-container').addClass('d-none');
		  var ausgabe = document.getElementById('cookie-info');
		  ausgabe.innerHTML = '(Cookies '+cookieAcceptanceState+')'; // BUG: immer Englisch
	  }
	
    $('.cookie-ok').click(function()  {
           $('.cookie-container').addClass('d-none');
            Cookies.set("CookiePolicy", "accepted");
            });
		
    $('.cookie-dismissed').click(function()  {
           $('.cookie-container').addClass('d-none');

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

