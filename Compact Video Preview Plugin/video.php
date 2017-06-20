<?php
/* 
Plugin Name: Compact Video Preview
Version: 1.0
Author: Valdas email: valdasns@gmail.com
Description: Preview videos in nice mode. 
*/

    add_action('init', 'register_shortcodes5');
	
	add_action( 'wp_enqueue_scripts', 'register_plugin_styles' );
	add_action( 'wp_enqueue_scripts', 'wptuts_scripts_basic' );
	add_action( 'wp_enqueue_scripts', 'wptuts_scripts_basic2' );
	
function register_shortcodes5(){
 
     add_shortcode( 'Addvideo', 'videoFunc' );
    }
    


function register_plugin_styles() {
	wp_register_style( 'add-videos', plugins_url( 'add-videos/Addvideosstyle.css' ) );
	wp_enqueue_style( 'add-videos' );
}
	
	
function wptuts_scripts_basic()
{
    /
    wp_register_script( 'jquery31', plugins_url( '/jquery31.js', __FILE__ ) );
   
    wp_enqueue_script( 'jquery31' );
}

	
function wptuts_scripts_basic2()
{
    wp_register_script( 'addvideoscript', plugins_url( '/addvideoscript.js', __FILE__ ));
    wp_enqueue_script( 'addvideoscript' );
}


function videoFunc($atts){
    
        $atts = shortcode_atts( array(
        'url' => '',
		'urls' => '',
         'title' => 'Be pavadinimo',
    ), $atts );
        
        $html = '';

        $videoName = $atts['title'];
        $videoUrl = $atts['url'];
		$videoUrlArr = $atts['urls'];
        
		if ($videoUrl){
		
        $html .= '<div class ="video" value="'.$videoUrl.'" value2="0">'.$videoName.'</div>';

		$html .='<div class = "singleVideo" ></div>';
		}
		if ($videoUrlArr){
		
        $html .= '<div class ="video" value="'.$videoUrlArr.'" value2="1">'.$videoName.'</div>';

		$html .='<div class = "singleVideo" ></div>';
		}

	return $html;}
	?>