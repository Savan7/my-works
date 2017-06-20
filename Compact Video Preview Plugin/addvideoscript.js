
$(document).ready(function(){
	$('.singleVideo').hide();
	$('.video').addClass('notactive');
	$('.video').on('click',function(){
			
			if ($(this).attr("value2")=="0"){
					$(this).next().html('<iframe src="'+$(this).attr("value")+'" frameborder="0" width="545" height="315" allowfullscreen></iframe>');
					$(this).toggleClass("open active notactive").next().slideToggle( "slow" );
			}
		   if ($(this).attr("value2")=="1"){

					var x = $(this).attr("value");
					var res = x.split(/\*/g);
					
					$(this).next().html('');
					
					for ( var i = 0, l = res.length; i < l; i++ ) {
						$(this).next().append('<iframe src="'+res[i]+'" frameborder="0" width="545" height="315" allowfullscreen></iframe>')
    
					}

						
						$(this).toggleClass("open active notactive").next().slideToggle( "slow" );
		   }
			   
	});
	});
