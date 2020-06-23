require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "RCTIPayNow"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["repository"]["url"]
  s.license      = package["license"]
  s.author       = package["author"]
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => package["repository"]["url"], :tag => "#{s.version}" }
  s.source_files = "ios/**/*.{h,m}"
  s.dependency "React"
  s.vendored_libraries = "ios/vendor/libipaynowCrossBorderPlugin.a"
  s.dependency 'WechatOpenSDK', '1.8.6'
  s.dependency 'AlipaySDK-iOS', '15.5.9'
  s.requires_arc = true
  s.frameworks = 'CoreGraphics','CoreTelephony','QuartzCore','SystemConfiguration','Security','Foundation','UIKit'
  s.library = 'z','sqlite3.0'
end
